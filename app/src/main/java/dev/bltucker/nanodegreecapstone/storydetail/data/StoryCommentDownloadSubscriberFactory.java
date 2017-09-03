package dev.bltucker.nanodegreecapstone.storydetail.data;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.storydetail.CommentRepository;

@ApplicationScope
class StoryCommentDownloadSubscriberFactory  {

    private CommentRepository commentRepository;
    private EventBus eventBus;

    @Inject
    public StoryCommentDownloadSubscriberFactory(CommentRepository commentRepository, EventBus eventBus){
        this.commentRepository = commentRepository;
        this.eventBus = eventBus;
    }

    StoryCommentDownloadSubscriber get(){
        return new StoryCommentDownloadSubscriber(commentRepository, eventBus);
    }
}
