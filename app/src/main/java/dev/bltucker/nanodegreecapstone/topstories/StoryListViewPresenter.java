package dev.bltucker.nanodegreecapstone.topstories;

import android.support.annotation.VisibleForTesting;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryListViewPresenter implements SwipeRefreshLayout.OnRefreshListener {

    private final ReadingSession readingSession;

    private final StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate;
    private final SyncRequestDelegate syncRequestDelegate;

    @VisibleForTesting
    StoryListView storyListView;

    @VisibleForTesting
    LoaderManager loaderManager;

    private final Tracker analyticsTracker;

    public StoryListViewPresenter(ReadingSession readingSession,
                                  StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate,
                                  SyncRequestDelegate syncRequestDelegate, Tracker tracker) {
        this.readingSession = readingSession;
        this.storyListLoaderCallbackDelegate = storyListLoaderCallbackDelegate;
        this.syncRequestDelegate = syncRequestDelegate;
        analyticsTracker = tracker;
    }

    public void onViewCreated(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        trackScreenView();
        this.loaderManager = loaderManager;
    }

    private void trackScreenView() {
        analyticsTracker.setScreenName("StoryListView");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onViewRestored(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        this.loaderManager = loaderManager;
    }

    private void setupView() {
        if (readingSession.hasStories()) {
            storyListView.showStories();

            if (readingSession.isStoryListIsDirty()) {
                loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate);
            }

        } else {
            storyListView.showLoadingSpinner();
            loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate);
        }
    }

    public void onViewResumed(StoryListView view) {
        setStoryListView(view);
        setupView();
    }

    public void onViewPaused() {
        setStoryListView(null);
    }

    public void onViewDestroyed() {
        setStoryListView(null);
        loaderManager = null;
    }

    public void onCommentsButtonClick(final Story story) {
        if (storyListView != null) {
            storyListView.showStoryDetailView(story);
        }
    }

    public void onReadStoryButtonClick(Story story) {
        if (storyListView != null && story != null) {
            storyListView.showStoryPostUrl(story.getUrl());
        }
    }

    private void setStoryListView(StoryListView view) {
        storyListView = view;
        storyListLoaderCallbackDelegate.setStoryListView(view);
    }

    @Override
    public void onRefresh() {
        syncRequestDelegate.sendSyncRequest();
    }

    public void onShowRefreshedStories() {
        readingSession.updateUserStoriesToLatestSync();
        storyListView.showStories();
    }
}
