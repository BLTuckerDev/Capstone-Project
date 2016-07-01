package dev.bltucker.nanodegreecapstone.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Observable;
import rx.functions.Func1;

public class CommentRepository {

    public static final int CACHE_SIZE = 3 * 1024 * 1024; // 3MiB

    @VisibleForTesting
    final HackerNewsApiService hackerNewsApiService;
    private final ContentResolver contentResolver;

    @VisibleForTesting
    final LruCache<Long, List<Comment>> commentLruCache;

    @Inject
    public CommentRepository(HackerNewsApiService hackerNewsApiService, ContentResolver contentResolver){
        this.hackerNewsApiService = hackerNewsApiService;
        this.contentResolver = contentResolver;
        commentLruCache = new LruCache<>(CACHE_SIZE);
    }

    public Observable<List<Comment>> getStoryComments(final long storyId) {
        List<Comment> cachedComments = commentLruCache.get(storyId);

        if (cachedComments != null) {
            return Observable.just(cachedComments);
        }

        final Long[] commentIds = getCommentIds(storyId);

        return Observable.from(commentIds)
                .concatMap(new Func1<Long, Observable<List<Comment>>>() {
                    @Override
                    public Observable<List<Comment>> call(Long aLong) {
                        return Observable.just(addCommentToList(aLong));
                    }
                });
    }

    Long[] getCommentIds(long storyId) {
        Cursor query = contentResolver.query(SchematicContentProviderGenerator.CommentRefs.withStoryId(String.valueOf(storyId)), null, null, null, null);

        if (null == query) {
            return new Long[0];
        }

        Long[] commentIds = new Long[query.getCount()];
        int index = 0;
        while (query.moveToNext()) {
            commentIds[index] = query.getLong(query.getColumnIndex(CommentRefsColumns._ID));
            index++;
        }

        query.close();
        return commentIds;
    }

    private List<Comment> addCommentToList(long commentId) {

        List<Comment> commentList = new ArrayList<>();
        Comment comment = hackerNewsApiService.getComment(commentId).toBlocking().first();

        if (comment.getAuthorName() != null && comment.getCommentText() != null && comment.getCommentText().length() > 0) {
            commentList.add(comment);
            if (comment.getReplyIds().length > 0) {
                for (int i = 0; i < comment.getReplyIds().length; i++) {
                    commentList.addAll(addCommentToList(comment.getReplyIds()[i]));
                }
            }
        }
        return commentList;
    }

    void clearInMemoryCache(){
        commentLruCache.evictAll();
    }

}
