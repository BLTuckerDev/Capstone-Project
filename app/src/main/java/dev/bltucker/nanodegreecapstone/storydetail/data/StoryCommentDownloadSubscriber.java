package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;


import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.storydetail.events.StoryCommentsDownloadCompleteEvent;
import dev.bltucker.nanodegreecapstone.models.Comment;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

class StoryCommentDownloadSubscriber implements Observer<Comment> {

    @VisibleForTesting
    CommentRepository commentRepository;

    @VisibleForTesting
    EventBus eventBus;

    @VisibleForTesting
    volatile boolean shouldContinueDownloadingComments = true;

    @Nullable
    private Disposable subscription;

    StoryCommentDownloadSubscriber(CommentRepository commentRepository, EventBus eventBus){
        this.commentRepository = commentRepository;
        this.eventBus = eventBus;
    }

    @Override
    public void onComplete() {
        Timber.d("StoryCommentDownloadSubscriberFactory onComplete()");
        eventBus.publish(new StoryCommentsDownloadCompleteEvent());
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "StoryCommentDownloadSubscriberFactory error");
    }

    @Override
    public void onSubscribe(Disposable d) {
        subscription = d;
    }

    @Override
    public void onNext(Comment comment) {
        commentRepository.saveComment(comment);
        if(!shouldContinueDownloadingComments && subscription != null){
            subscription.dispose();
        }
    }

    public void setShouldContinueDownloadingComments(boolean shouldContinueDownloadingComments) {
        this.shouldContinueDownloadingComments = shouldContinueDownloadingComments;
    }

    boolean isUnsubscribed() {
        return subscription == null || subscription.isDisposed();

    }

    void unsubscribe() {
        if(subscription != null && !subscription.isDisposed()){
            subscription.dispose();
        }
    }
}
