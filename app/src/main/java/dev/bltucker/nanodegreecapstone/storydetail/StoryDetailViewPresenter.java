package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import rx.Completable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StoryDetailViewPresenter {
    private final ContentResolver contentResolver;
    private final Tracker analyticsTracker;
    private final StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate;

    private StoryDetailView view;
    private LoaderManager loaderManager;

    public StoryDetailViewPresenter(ContentResolver contentResolver, Tracker analyticsTracker, StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate, LoaderManager loaderManager) {
        this.contentResolver = contentResolver;
        this.analyticsTracker = analyticsTracker;
        this.commentLoaderCallbackDelegate = commentLoaderCallbackDelegate;
        this.loaderManager = loaderManager;
    }


    public void onViewCreated(StoryDetailView detailView, DetailStory detailStory) {
        setDetailView(detailView);
        trackScreenView();

        if (detailStory.hasStory()) {
            view.showStory();
            initializeCommentLoader(detailStory);
        } else {
            view.showEmptyView();
        }

    }

    public void onViewRestored(StoryDetailView detailView, DetailStory detailStory) {
        setDetailView(detailView);

        if (detailStory.hasStory()) {
            view.showStory();
            initializeCommentLoader(detailStory);
        } else {
            view.showEmptyView();
        }
    }

    private void initializeCommentLoader(DetailStory story) {
        Timber.d("initializeCommentLoader");
        Bundle loaderBundle = new Bundle();
        loaderBundle.putParcelable(StoryCommentsLoader.SELECTED_DETAIL_STORY, story);
        this.loaderManager.initLoader(StoryCommentsLoader.STORY_COMMENT_LOADER, loaderBundle, commentLoaderCallbackDelegate);
    }

    private void trackScreenView() {
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
        if (view != null) {
            view.showStoryPostUrl();
        }
    }

    public void onSaveStoryClick(DetailStory detailStory) {
        if (null == detailStory) {
            return;
        }

        final ReadLaterStory saveMe = new ReadLaterStory(detailStory.getStoryId(), detailStory.getPosterName(), detailStory.getTitle(), detailStory.getUrl());
        Completable.fromAction(new Action0() {
            @Override
            public void call() {

                Cursor query = contentResolver.query(SchematicContentProviderGenerator.ReadLaterStoryPaths.withStoryId(String.valueOf(saveMe.getId())),
                        null,
                        null,
                        null,
                        null);

                if (null == query) {
                    return;
                }

                if (query.getCount() > 0) {
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
                        if (view != null) {
                            view.showStorySaveConfirmation();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }
}
