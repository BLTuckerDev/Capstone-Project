package dev.bltucker.nanodegreecapstone.topstories;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.data.StoryListLoader;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class StoryListViewPresenter implements SwipeRefreshLayout.OnRefreshListener {

    private final ReadingSession readingSession;
    private final EventBus eventBus;

    private final StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate;
    private final Account account;

    private StoryListView storyListView;
    private Subscription syncCompletedEventSubscription;

    private LoaderManager loaderManager;
    private final Tracker analyticsTracker;

    public StoryListViewPresenter(ReadingSession readingSession,
                                  EventBus eventBus,
                                  StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate,
                                  Account account, Tracker tracker) {
        this.readingSession = readingSession;
        this.eventBus = eventBus;
        this.storyListLoaderCallbackDelegate = storyListLoaderCallbackDelegate;
        this.account = account;
        analyticsTracker = tracker;
        subscribeToSyncAdapterEvents();
    }

    public void onViewCreated(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        trackScreenView();
        this.loaderManager = loaderManager;
        setupView();
    }

    private void trackScreenView(){
        analyticsTracker.setScreenName("StoryListView");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onViewRestored(StoryListView view, LoaderManager loaderManager) {
        setStoryListView(view);
        this.loaderManager = loaderManager;
        setupView();
    }

    private void setupView() {
        final boolean hasStories = readingSession.hasStories();
        final boolean sessionIsDirty = readingSession.isStoryListIsDirty();

        if(!hasStories && sessionIsDirty){
            storyListView.showLoadingSpinner();
            readingSession.setStoryListIsDirty(false);
            this.loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate);
        } else if(!hasStories && !sessionIsDirty){
            storyListView.showLoadingSpinner();
        } else if(hasStories && !sessionIsDirty){
            storyListView.hideLoadingSpinner();
            storyListView.showStories();
        } else if(hasStories && sessionIsDirty){
            storyListView.hideLoadingSpinner();
            storyListView.showStories();
            readingSession.setStoryListIsDirty(false);
            this.loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate);
        }
    }

    public void onViewResumed(StoryListView view) {
        setStoryListView(view);
    }

    public void onViewPaused(StoryListView view) {
        setStoryListView(null);
    }

    public void onViewDestroyed(StoryListView view){
        setStoryListView(null);
        loaderManager = null;
    }

    public void onCommentsButtonClick(final int storyPosition) {
        if(storyListView != null){
            readingSession.clearCurrentStory();
            storyListView.showStoryDetailView(storyPosition);
        }
    }

    public void onReadStoryButtonClick(int storyPosition) {
        if(storyListView != null){
            storyListView.showStoryPostUrl(readingSession.getStory(storyPosition).getUrl());
        }
    }

    private void setStoryListView(StoryListView view) {
        storyListView = view;
        storyListLoaderCallbackDelegate.setStoryListView(view);
    }

    private void subscribeToSyncAdapterEvents() {
        syncCompletedEventSubscription = eventBus.subscribeTo(SyncCompletedEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {  }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error thrown after a sync complete event handler");
                    }

                    @Override
                    public void onNext(Object o) {
                        Timber.d("StoryListViewPresenter syncCompleteHandler");
                        if(storyListView != null){
                            loaderManager.restartLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate);
                        } else {
                            readingSession.setStoryListIsDirty(true);
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, SchematicContentProviderGenerator.AUTHORITY, bundle);
    }
}
