package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.data.StoryListLoader;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Subscriber;
import rx.Subscription;

public class StoryListViewPresenter {

    static final String SELECTED_STORY_BUNDLE_KEY = "story";

    private final StoryRepository storyRepository;
    private final ReadingSession readingSession;
    private final EventBus eventBus;
    private final StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate = new StoryListLoaderCallbackDelegate(this);
    private final StoryCommentLoaderCallbackDelegate storyCommentLoaderCallbackDelegate = new StoryCommentLoaderCallbackDelegate(this);

    private StoryListView storyListView;
    private Subscription syncCompletedEventSubscription;

    private final Loader storyListLoader;
    private final StoryCommentsLoader commentsLoader;
    private LoaderManager loaderManager;

    @Inject
    public StoryListViewPresenter(StoryRepository storyRepository, ReadingSession readingSession, EventBus eventBus, StoryListLoader storyListLoader, StoryCommentsLoader commentsLoader) {
        this.storyRepository = storyRepository;
        this.readingSession = readingSession;
        this.eventBus = eventBus;
        this.storyListLoader = storyListLoader;
        this.commentsLoader = commentsLoader;
    }


    public void onViewCreated(StoryListView view, LoaderManager loaderManager) {
        storyListView = view;
        this.loaderManager = loaderManager;
        if(readingSession.getStories().isEmpty()){
            this.loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate).forceLoad();
        } else {
            view.showStories(readingSession.getStories());
        }
    }

    public void onViewRestored(StoryListView view, LoaderManager loaderManager) {
        storyListView = view;
        this.loaderManager = loaderManager;

        if(readingSession.getStories().isEmpty()){
            this.loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate).forceLoad();
        } else {
            view.showStories(readingSession.getStories());
        }
    }

    public void onViewResumed(StoryListView view) {
        storyListView = view;
        //TODO consider moiving this to create/restore
        //and remembering to update the view after it is resumed if an update occurred while it was
        //"paused"
        syncCompletedEventSubscription = eventBus.subscribeTo(SyncCompletedEvent.class)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {  }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Object o) {
                        handleOnSyncCompleteEvent();
                    }
                });
    }

    public void onViewPaused(StoryListView view) {
        storyListView = null;
        syncCompletedEventSubscription.unsubscribe();
    }

    public void onViewDestroyed(StoryListView view){
        loaderManager = null;
    }

    private void handleOnSyncCompleteEvent() {
        //TODO restart the loader
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

    StoryListView getStoryListView() {
        return storyListView;
    }

    Loader getStoryListLoader() {
        return storyListLoader;
    }

    ReadingSession getReadingSession() {
        return readingSession;
    }

    StoryCommentsLoader getCommentsLoader() {
        return commentsLoader;
    }
}
