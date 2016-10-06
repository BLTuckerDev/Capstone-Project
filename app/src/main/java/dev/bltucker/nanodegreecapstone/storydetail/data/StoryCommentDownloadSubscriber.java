package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.support.annotation.VisibleForTesting;

import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.StoryCommentsDownloadCompleteEvent;
import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Subscriber;
import timber.log.Timber;

class StoryCommentDownloadSubscriber extends Subscriber<Comment> {

    @VisibleForTesting
    CommentRepository commentRepository;

    @VisibleForTesting
    EventBus eventBus;

    private volatile boolean shouldContinueDownloadingComments = true;

    StoryCommentDownloadSubscriber(CommentRepository commentRepository, EventBus eventBus){
        this.commentRepository = commentRepository;
        this.eventBus = eventBus;
    }

    @Override
    public void onCompleted() {
        Timber.d("StoryCommentDownloadSubscriberFactory onComplete()");
        eventBus.publish(new StoryCommentsDownloadCompleteEvent());
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "StoryCommentDownloadSubscriberFactory error");
    }

    @Override
    public void onNext(Comment comment) {
        commentRepository.saveComment(comment);
        if(!shouldContinueDownloadingComments){
            unsubscribe();
        }
    }

    public void setShouldContinueDownloadingComments(boolean shouldContinueDownloadingComments) {
        this.shouldContinueDownloadingComments = shouldContinueDownloadingComments;
    }
}
