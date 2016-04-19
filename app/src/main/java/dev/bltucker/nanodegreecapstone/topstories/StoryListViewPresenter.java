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
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

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
        subscribeToSyncAdapterEvents();
    }


    public void onViewCreated(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        this.loaderManager = loaderManager;
        if(readingSession.getStories().isEmpty()){
            forceStoryListReload();
        }
    }

    public void onViewRestored(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        this.loaderManager = loaderManager;
        if(readingSession.getStories().isEmpty()){
            forceStoryListReload();
        }
    }

    public void onViewResumed(StoryListView view) {
        setStoryListView(view);
        this.storyListView.showStories(readingSession.getStories());
    }

    public void onViewPaused(StoryListView view) {
        setStoryListView(null);
    }

    public void onViewDestroyed(StoryListView view){
        setStoryListView(null);
        loaderManager = null;
    }

    private void handleOnSyncCompleteEvent() {
        Timber.d("StoryListViewPresenter syncCompleteHandler");
        forceStoryListReload();
    }

    public void onCommentsButtonClick(final Story selectedStory) {
        if(storyListView != null && this.loaderManager != null){
            storyListView.showLoadingView();

            Bundle argBundle = new Bundle();
            argBundle.putParcelable(SELECTED_STORY_BUNDLE_KEY, selectedStory);
            this.loaderManager.restartLoader(StoryCommentsLoader.STORY_COMMENT_LOADER, argBundle, storyCommentLoaderCallbackDelegate).forceLoad();
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
        this.loaderManager.restartLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate).forceLoad();
    }

    private void subscribeToSyncAdapterEvents() {
        syncCompletedEventSubscription = eventBus.subscribeTo(SyncCompletedEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
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
}
