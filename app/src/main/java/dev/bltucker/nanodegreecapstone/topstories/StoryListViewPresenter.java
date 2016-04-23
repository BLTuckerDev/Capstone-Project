package dev.bltucker.nanodegreecapstone.topstories;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
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

public class StoryListViewPresenter implements SwipeRefreshLayout.OnRefreshListener {

    static final String SELECTED_STORY_BUNDLE_KEY = "story";

    private final ReadingSession readingSession;
    private final EventBus eventBus;

    private final StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate;
    private final StoryCommentLoaderCallbackDelegate storyCommentLoaderCallbackDelegate;
    private final Account account;

    private StoryListView storyListView;
    private Subscription syncCompletedEventSubscription;

    private LoaderManager loaderManager;

    public StoryListViewPresenter(ReadingSession readingSession, EventBus eventBus, StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate,
                                  StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate, Account account) {
        this.readingSession = readingSession;
        this.eventBus = eventBus;
        this.storyListLoaderCallbackDelegate = storyListLoaderCallbackDelegate;
        this.storyCommentLoaderCallbackDelegate = commentLoaderCallbackDelegate;
        this.account = account;
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
        if(readingSession.isStoryListIsDirty()){
            forceStoryListReload();
        }
    }

    public void onViewPaused(StoryListView view) {
        setStoryListView(null);
    }

    public void onViewDestroyed(StoryListView view){
        setStoryListView(null);
        loaderManager = null;
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
        Timber.d("forceStoryListReload");
        this.loaderManager.restartLoader(StoryListLoader.STORY_LIST_LOADER, null, storyListLoaderCallbackDelegate).forceLoad();
        readingSession.setStoryListIsDirty(false);
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
                            forceStoryListReload();
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
