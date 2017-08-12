package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import dev.bltucker.nanodegreecapstone.data.daos.ReadLaterStoryDao;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import dev.bltucker.nanodegreecapstone.storydetail.data.StoryCommentLoaderCallbackDelegate;
import dev.bltucker.nanodegreecapstone.storydetail.data.StoryCommentsLoader;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class StoryDetailViewPresenter {
    private final StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate;

    private StoryDetailView view;
    private LoaderManager loaderManager;

    private final ReadLaterStoryDao readLaterStoryDao;

    public StoryDetailViewPresenter(StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate,
                                    LoaderManager loaderManager,
                                    ReadLaterStoryDao readLaterStoryDao) {
        this.commentLoaderCallbackDelegate = commentLoaderCallbackDelegate;
        this.loaderManager = loaderManager;
        this.readLaterStoryDao = readLaterStoryDao;
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
        commentLoaderCallbackDelegate.setDetailStory(story);
        this.loaderManager.initLoader(StoryCommentsLoader.STORY_COMMENT_LOADER, loaderBundle, commentLoaderCallbackDelegate);
    }

    private void trackScreenView() {
        //TODO implement with firebase analytics
    }


    private void setDetailView(StoryDetailView detailView) {
        view = detailView;
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
        Completable.fromAction(() -> readLaterStoryDao.saveStory(saveMe)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (view != null) {
                        view.showStorySaveConfirmation();
                    }
                }, e -> Timber.e(e, "Error while attempting to save a read later story."));
    }
}
