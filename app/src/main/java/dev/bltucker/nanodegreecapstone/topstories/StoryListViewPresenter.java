package dev.bltucker.nanodegreecapstone.topstories;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryListViewPresenter implements SwipeRefreshLayout.OnRefreshListener {

    private final ReadingSession readingSession;

    private final StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate;
    private final Account account;

    private StoryListView storyListView;

    private LoaderManager loaderManager;
    private final Tracker analyticsTracker;

    public StoryListViewPresenter(ReadingSession readingSession,
                                  StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate,
                                  Account account, Tracker tracker) {
        this.readingSession = readingSession;
        this.storyListLoaderCallbackDelegate = storyListLoaderCallbackDelegate;
        this.account = account;
        analyticsTracker = tracker;
    }

    public void onViewCreated(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        trackScreenView();
        this.loaderManager = loaderManager;
    }

    private void trackScreenView(){
        analyticsTracker.setScreenName("StoryListView");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onViewRestored(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        this.loaderManager = loaderManager;
    }

    private void setupView() {
        if(readingSession.hasStories()){
            storyListView.showStories();

            if(readingSession.isStoryListIsDirty()){
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

    public void onViewDestroyed(){
        setStoryListView(null);
        loaderManager = null;
    }

    public void onCommentsButtonClick(final Story story) {
        if(storyListView != null){
            storyListView.showStoryDetailView(story);
        }
    }

    public void onReadStoryButtonClick(Story story) {
        if(storyListView != null){
            if(story != null){
                storyListView.showStoryPostUrl(story.getUrl());
            }
        }
    }

    private void setStoryListView(StoryListView view) {
        storyListView = view;
        storyListLoaderCallbackDelegate.setStoryListView(view);
    }

    @Override
    public void onRefresh() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, SchematicContentProviderGenerator.AUTHORITY, bundle);
    }

    public void onShowRefreshedStories() {
        readingSession.updateUserStoriesToLatestSync();
        storyListView.showStories();
    }
}
