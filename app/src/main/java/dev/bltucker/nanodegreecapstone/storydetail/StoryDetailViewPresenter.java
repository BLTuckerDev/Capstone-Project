package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import timber.log.Timber;

public class StoryDetailViewPresenter {
    static final String SELECTED_STORY_BUNDLE_KEY = "story";

    private final ReadingSession readingSession;
    private final Tracker analyticsTracker;
    private final StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate;

    private StoryDetailView view;
    private LoaderManager loaderManager;

    public StoryDetailViewPresenter(ReadingSession readingSession, Tracker analyticsTracker,
                                    StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate){
        this.readingSession = readingSession;
        this.analyticsTracker = analyticsTracker;
        this.commentLoaderCallbackDelegate = commentLoaderCallbackDelegate;
    }


    public void onViewCreated(StoryDetailView detailView, LoaderManager loaderManager, int storyPosition) {
        setDetailView(detailView);
        this.loaderManager = loaderManager;
        trackScreenView();
        forceCommentReload(storyPosition);
    }

    private void forceCommentReload(int storyPosition) {
        Timber.d("forceCommentReload");
        Story selectedStory = readingSession.getStory(storyPosition);
        Bundle loaderBundle = new Bundle();
        loaderBundle.putParcelable(SELECTED_STORY_BUNDLE_KEY, selectedStory);
        this.loaderManager.restartLoader(StoryCommentsLoader.STORY_COMMENT_LOADER, loaderBundle, commentLoaderCallbackDelegate).forceLoad();
    }

    private void trackScreenView(){
        analyticsTracker.setScreenName("StoryDetailView");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onViewRestored(StoryDetailView detailView, LoaderManager loaderManager) {
        setDetailView(detailView);
        this.loaderManager = loaderManager;
        view.showStory();
        view.showComments();
    }

    private void setDetailView(StoryDetailView detailView) {
        view = detailView;
        commentLoaderCallbackDelegate.setStoryDetailView(detailView);
    }

    public void onViewResumed(StoryDetailView detailView) {
        view = detailView;
    }

    public void onViewPaused() {
        view = null;
    }

    public void onViewDestroyed() {
        loaderManager = null;
    }

    public void onReadButtonClicked() {
        if(view != null){
            view.showStoryPostUrl(readingSession.getCurrentStory().getUrl());
        }
    }
}
