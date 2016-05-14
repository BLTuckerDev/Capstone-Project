package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Completable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StoryDetailViewPresenter {
    static final String SELECTED_STORY_BUNDLE_KEY = "story";

    private final Context context;
    private final ReadingSession readingSession;
    private final Tracker analyticsTracker;
    private final StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate;

    private StoryDetailView view;
    private LoaderManager loaderManager;

    public StoryDetailViewPresenter(Context context, ReadingSession readingSession, Tracker analyticsTracker,
                                    StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate){
        this.context = context;
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

    public void onSaveStoryClick() {
        Story selectedStory = readingSession.getCurrentStory();
        final ReadLaterStory saveMe = new ReadLaterStory(selectedStory.getId(), selectedStory.getPosterName(), selectedStory.getTitle(), selectedStory.getUrl());
        Completable.fromAction(new Action0() {
            @Override
            public void call() {

                Cursor query = context.getContentResolver().query(SchematicContentProviderGenerator.ReadLaterStoryPaths.withStoryId(String.valueOf(saveMe.getId())),
                        null,
                        null,
                        null,
                        null);

                if(query.getCount() > 0){
                    return;
                }

                ContentValues cv = ReadLaterStory.mapToContentValues(saveMe);
                context.getContentResolver().insert(SchematicContentProviderGenerator.ReadLaterStoryPaths.ALL_READ_LATER_STORIES, cv);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onNext(Object o) {
                        if(view != null){
                            view.showStorySaveConfirmation();
                        }
                    }
                });
    }
}
