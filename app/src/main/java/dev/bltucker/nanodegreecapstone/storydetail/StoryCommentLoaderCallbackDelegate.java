package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import javax.inject.Provider;

import timber.log.Timber;

class StoryCommentLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks<Void> {

    private final Provider<StoryCommentsLoader> commentsLoaderProvider;
    private StoryDetailView storyDetailView;

    public StoryCommentLoaderCallbackDelegate(Provider<StoryCommentsLoader> commentsLoaderProvider) {
        this.commentsLoaderProvider = commentsLoaderProvider;
    }

    public void setStoryDetailView(StoryDetailView view){
        this.storyDetailView = view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        StoryCommentsLoader commentsLoader = commentsLoaderProvider.get();
        commentsLoader.setDetailStory((DetailStory) args.getParcelable(StoryCommentsLoader.SELECTED_DETAIL_STORY));
        return commentsLoader;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, final Void data) {
        Timber.d("Loader Finished");
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Timber.d("Loader reset");
    }
}