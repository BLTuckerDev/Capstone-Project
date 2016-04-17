package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.data.StoryListLoader;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Subscriber;
import rx.Subscription;

public class StoryListViewPresenter {

    static final String SELECTED_STORY_BUNDLE_KEY = "story";

    private final ReadingSession readingSession;
    private final EventBus eventBus;

    private final StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate;
    private final StoryCommentLoaderCallbackDelegate storyCommentLoaderCallbackDelegate;

    private StoryListView storyListView;
    private Subscription syncCompletedEventSubscription;

    private LoaderManager loaderManager;

    public StoryListViewPresenter(ReadingSession readingSession, EventBus eventBus, StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate, StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate) {
        this.readingSession = readingSession;
        this.eventBus = eventBus;
        this.storyListLoaderCallbackDelegate = storyListLoaderCallbackDelegate;
        this.storyCommentLoaderCallbackDelegate = commentLoaderCallbackDelegate;
    }


    public void onViewCreated(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        this.loaderManager = loaderManager;
        if(readingSession.getStories().isEmpty()){
            forceStoryListReload();
        } else {
            view.showStories(readingSession.getStories());
        }
    }

    public void onViewRestored(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        this.loaderManager = loaderManager;

        if(readingSession.getStories().isEmpty()){
            forceStoryListReload();
        } else {
            view.showStories(readingSession.getStories());
        }
    }

    public void onViewResumed(StoryListView view) {
        setStoryListView(view);
        //TODO consider moiving this to create/restore
        //and remembering to update the view after it is resumed if an update occurred while it was
        //"paused"
        syncCompletedEventSubscription = eventBus.subscribeTo(SyncCompletedEvent.class)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {  }

                    @Override
                    public void onError(Throwable e) {  }

                    @Override
                    public void onNext(Object o) {
                        handleOnSyncCompleteEvent();
                    }
                });
    }

    public void onViewPaused(StoryListView view) {
        setStoryListView(null);
        syncCompletedEventSubscription.unsubscribe();
    }

    public void onViewDestroyed(StoryListView view){
        setStoryListView(null);
        loaderManager = null;
    }

    private void handleOnSyncCompleteEvent() {
        forceStoryListReload();
    }

    public void onCommentsButtonClick(final Story selectedStory) {
        if(storyListView != null && this.loaderManager != null){
            storyListView.showLoadingView();

            Bundle argBundle = new Bundle();
            argBundle.putParcelable(SELECTED_STORY_BUNDLE_KEY, selectedStory);
            this.loaderManager.initLoader(StoryCommentsLoader.STORY_COMMENT_LOADER, argBundle, storyCommentLoaderCallbackDelegate).forceLoad();
        }
    }

    public void onReadStoryButtonClick(Story story) {
        if(storyListView != null){
            storyListView.showStoryPostUrl(story.getUrl());
        }
    }

    private void setStoryListView(StoryListView view) {
        storyListView = view;
        storyListLoaderCallbackDelegate.setStoryListView(view);
        storyCommentLoaderCallbackDelegate.setStoryListView(view);
    }

    private void forceStoryListReload() {
        this.loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate).forceLoad();
    }
}
