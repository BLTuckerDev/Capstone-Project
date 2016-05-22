package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.models.Comment;
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

    private final ContentResolver contentResolver;
    private final ReadingSession readingSession;
    private final Tracker analyticsTracker;
    private final StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate;

    private StoryDetailView view;
    private LoaderManager loaderManager;

    public StoryDetailViewPresenter(ContentResolver contentResolver, ReadingSession readingSession, Tracker analyticsTracker,
                                    StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate, LoaderManager loaderManager){
        this.contentResolver = contentResolver;
        this.readingSession = readingSession;
        this.analyticsTracker = analyticsTracker;
        this.commentLoaderCallbackDelegate = commentLoaderCallbackDelegate;
        this.loaderManager = loaderManager;
    }


    public void onViewCreated(StoryDetailView detailView, int storyPosition) {
        setDetailView(detailView);
        trackScreenView();

        if(storyPosition == -1){
            view.showEmptyView();
        } else {
            Story selectedStory = readingSession.getStory(storyPosition);
            readingSession.read(selectedStory, new ArrayList<Comment>());
            view.showStory();
            initializeCommentLoader(selectedStory);
        }
    }

    public void onViewRestored(StoryDetailView detailView) {
        setDetailView(detailView);
         view.showStory();
        initializeCommentLoader(readingSession.getCurrentStory());
    }

    private void initializeCommentLoader(Story story) {
        Timber.d("initializeCommentLoader");
        Bundle loaderBundle = new Bundle();
        loaderBundle.putParcelable(SELECTED_STORY_BUNDLE_KEY, story);
        this.loaderManager.initLoader(StoryCommentsLoader.STORY_COMMENT_LOADER, loaderBundle, commentLoaderCallbackDelegate);
    }

    private void trackScreenView(){
        analyticsTracker.setScreenName("StoryDetailView");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
    }

    public void onReadButtonClicked() {
        if(view != null){
            view.showStoryPostUrl(readingSession.getCurrentStory().getUrl());
        }
    }

    public void onSaveStoryClick() {
        Story selectedStory = readingSession.getCurrentStory();

        if(null == selectedStory){
            view.showSelectAStoryPrompt();
            return;
        }

        final ReadLaterStory saveMe = new ReadLaterStory(selectedStory.getId(), selectedStory.getPosterName(), selectedStory.getTitle(), selectedStory.getUrl());
        Completable.fromAction(new Action0() {
            @Override
            public void call() {

                Cursor query = contentResolver.query(SchematicContentProviderGenerator.ReadLaterStoryPaths.withStoryId(String.valueOf(saveMe.getId())),
                        null,
                        null,
                        null,
                        null);

                if(query.getCount() > 0){
                    query.close();
                    return;
                }

                ContentValues cv = ReadLaterStory.mapToContentValues(saveMe);
                contentResolver.insert(SchematicContentProviderGenerator.ReadLaterStoryPaths.ALL_READ_LATER_STORIES, cv);
                query.close();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        if(view != null){
                            view.showStorySaveConfirmation();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onNext(Object o) { }
                });
    }
}
