package dev.bltucker.nanodegreecapstone.storydetail;

import android.support.annotation.NonNull;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Comment;
import io.reactivex.Observable;
import io.reactivex.Single;

@ApplicationScope
public class CommentRepository {

    @NonNull
    private final CommentsDao commentsDao;

    @NonNull
    private final HackerNewsApiService hackerNewsApiService;

    @Inject
    public CommentRepository(@NonNull CommentsDao commentsDao,
                             @NonNull HackerNewsApiService hackerNewsApiService){
        this.commentsDao = commentsDao;
        this.hackerNewsApiService = hackerNewsApiService;
    }

    public Observable<Comment[]> getCommentsForStoryId(long storyId){
        return Observable.concat(getLocalComments(storyId), getRemoteComments(storyId));
    }

    public void saveComment(Comment comment){
        commentsDao.save(comment);
    }

    private Observable<Comment[]> getRemoteComments(final long storyId) {
        return hackerNewsApiService.getStory(storyId)
                .toObservable()
                .flatMap(story -> Observable.just(story.commentIds))
                .concatMap(topLevelCommentIds -> {
                    long[] commentIds = new long[topLevelCommentIds.length];
                    for (int i = 0; i < commentIds.length; i++) {
                        commentIds[i] = topLevelCommentIds[i];
                    }
                    return downloadComments(storyId, commentIds, 0);
                })
                .filter(comment -> comment.getCommentText().trim().length() > 0)
                .toList()
                .doOnSuccess(comments -> {
                    Log.d("comments", "Saving the comments!");
                    //TODO make sure that we have onConflict replace set for comments
                    commentsDao.saveAll(comments);
                })
                .flatMap(comments -> Single.just(comments.toArray(new Comment[0])))
                .toObservable();
    }

    private Observable<Comment> downloadComments( long storyId, long[] commentIds, int commentDepth){
        if (commentIds == null || commentIds.length == 0) {
            return Observable.empty();
        } else {
            return Observable.range(0, commentIds.length)
                    .concatMap(index -> hackerNewsApiService.getComment(commentIds[index]).toObservable())
                    .concatMap(commentDto -> {
                        final int childDepth = commentDepth + 1;
                        return Observable.just(new Comment(commentDto.id, storyId, commentDto.by, commentDto.text, commentDto.time, commentDto.parent, commentDepth))
                                .mergeWith(downloadComments(storyId, commentDto.kids, childDepth));
                    })
                    .filter(comment -> comment.getCommentText().trim().length() > 0);
        }
    }

    private Observable<Comment[]> getLocalComments(long storyId) {
        return Observable.just(commentsDao.getStoryComments(storyId));
    }

}
