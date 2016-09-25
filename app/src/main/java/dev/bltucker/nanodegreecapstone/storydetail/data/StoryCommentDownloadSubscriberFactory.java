package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.content.ContentResolver;

import javax.inject.Inject;
import javax.inject.Singleton;

import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;

@ApplicationScope
class StoryCommentDownloadSubscriberFactory  {

    private ContentResolver contentResolver;
    private EventBus eventBus;

    @Inject
    public StoryCommentDownloadSubscriberFactory(ContentResolver contentResolver, EventBus eventBus){
        this.contentResolver = contentResolver;
        this.eventBus = eventBus;
    }

    StoryCommentDownloadSubscriber get(){
        return new StoryCommentDownloadSubscriber(contentResolver, eventBus);
    }
}
