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

    private Observable<Comment[]> getLocalComments(long storyId) {
        //noinspection Convert2MethodRef
        return commentsDao.getStoryCommentsFlowable(storyId)
                .onBackpressureDrop()
                .filter(comments -> comments != null)
                .toObservable();
    }

}
