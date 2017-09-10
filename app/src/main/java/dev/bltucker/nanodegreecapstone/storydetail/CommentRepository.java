package dev.bltucker.nanodegreecapstone.storydetail;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Comment;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

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
        return getLocalComments(storyId);
    }

    public void saveComment(Comment comment){
        commentsDao.save(comment);
    }

    public void syncLatestStoryComments(final long storyId){
        hackerNewsApiService.getStory(storyId)
                .flatMapObservable(story -> Observable.just(story.commentIds))
                .concatMap(topLevelCommentIds -> {
                    long[] commentIds = new long[topLevelCommentIds.length];
                    for (int i = 0; i < commentIds.length; i++) {
                        commentIds[i] = topLevelCommentIds[i];
                    }
                    return downloadComments(storyId, commentIds, 0);
                })
                .filter(comment -> comment.getCommentText().trim().length() > 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Comment>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if(d != null && !d.isDisposed()){
                            d.dispose();
                        }
                    }

                    @Override
                    public void onNext(Comment comment) {
                        commentsDao.save(comment);
                    }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onComplete() { }
                });
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
        //noinspection Convert2MethodRef
        return commentsDao.getStoryCommentsFlowable(storyId)
                .onBackpressureDrop()
                .filter(comments -> comments != null)
                .toObservable();
    }

}
