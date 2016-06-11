package dev.bltucker.nanodegreecapstone.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class NetworkAndContentProviderBackedStoryRepository implements StoryRepository {

    public static final int CACHE_SIZE = 2 * 1024 * 1024; // 2MiB

    @VisibleForTesting
    final ContentResolver contentResolver;

    @VisibleForTesting
    final HackerNewsApiService hackerNewsApiService;

    @VisibleForTesting
    final LruCache<Long, List<Comment>> commentLruCache;

    @Inject
    public NetworkAndContentProviderBackedStoryRepository(ContentResolver contentResolver, HackerNewsApiService hackerNewsApiService) {
        this.contentResolver = contentResolver;
        this.hackerNewsApiService = hackerNewsApiService;
        commentLruCache = new LruCache<>(CACHE_SIZE);
    }

    @Override
    public Observable<List<Story>> getAllStories() {

        Cursor queryCursor = contentResolver.query(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES,
                null,
                null,
                null,
                null);

        if (null == queryCursor) {
            return Observable.error(new Exception("Query for all stories returned a null cursor"));
        }

        return Observable.just(queryCursor)
                .map(new Func1<Cursor, List<Story>>() {
                    @Override
                    public List<Story> call(Cursor cursor) {
                        List<Story> storyList = new ArrayList<>(cursor.getCount());

                        while (cursor.moveToNext()) {
                            long storyId = cursor.getLong(cursor.getColumnIndex(StoryColumns._ID));
                            String storyPoster = cursor.getString(cursor.getColumnIndex(StoryColumns.POSTER_NAME));
                            long score = cursor.getLong(cursor.getColumnIndex(StoryColumns.SCORE));
                            String title = cursor.getString(cursor.getColumnIndex(StoryColumns.TITLE));
                            long unixTime = cursor.getLong(cursor.getColumnIndex(StoryColumns.UNIX_TIME));
                            String storyUrl = cursor.getString(cursor.getColumnIndex(StoryColumns.URL));
                            Long[] commentIds = getCommentIds(storyId);

                            storyList.add(new Story(storyId, storyPoster, score, unixTime, title, storyUrl, commentIds));
                        }

                        cursor.close();
                        return storyList;
                    }
                });
    }

    private Long[] getCommentIds(long storyId) {
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

    @Override
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

    public List<Comment> addCommentToList(long commentId) {

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

    @Override
    public void saveStories(List<Story> stories) {
        commentLruCache.evictAll();
        int deletedStories = contentResolver.delete(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, null, null);
        int deletedComments = contentResolver.delete(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS, null, null);

        Timber.d("Deleted %d stories", deletedStories);
        Timber.d("Deleted %d comments", deletedComments);

        List<ContentValues> storyContentValues = new ArrayList<>(stories.size());
        List<ContentValues> commentRefsContentValuesList = new ArrayList<>();

        for (int i = 0; i < stories.size(); i++) {
            Story story = stories.get(i);
            storyContentValues.add(Story.mapToContentValues(story));
            commentRefsContentValuesList.addAll(getCommentRefList(story));
        }

        ContentValues[] contentValues = new ContentValues[storyContentValues.size()];
        int insertedStoryCount = contentResolver.bulkInsert(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, storyContentValues.toArray(contentValues));

        Timber.d("Inserted %d stories", insertedStoryCount);

        ContentValues[] commentRefsContentValuesArray = new ContentValues[commentRefsContentValuesList.size()];
        int commentRefInsertCount = contentResolver.bulkInsert(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS, commentRefsContentValuesList.toArray(commentRefsContentValuesArray));

        Timber.d("Inserted %d comment references", commentRefInsertCount);
    }

    private List<ContentValues> getCommentRefList(Story story) {
        Long[] commentIds = story.getCommentIds();
        List<ContentValues> contentValues = new ArrayList<>(commentIds.length);

        for (int i = 0; i < commentIds.length; i++) {
            ContentValues aContentValue = new ContentValues();
            aContentValue.put(CommentRefsColumns._ID, commentIds[i]);
            aContentValue.put(CommentRefsColumns.STORY_ID, story.getId());
            aContentValue.put(CommentRefsColumns.READ_RANK, i);
            contentValues.add(aContentValue);
        }

        return contentValues;
    }
}
