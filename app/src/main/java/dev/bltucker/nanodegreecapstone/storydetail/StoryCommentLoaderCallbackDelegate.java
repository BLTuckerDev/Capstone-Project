package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.models.Comment;
import timber.log.Timber;

class StoryCommentLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks<List<Comment>> {

    private final Provider<StoryCommentsLoader> commentsLoaderProvider;
    private StoryDetailView storyDetailView;

    @Nullable
    private DetailStory detailStory;

    public StoryCommentLoaderCallbackDelegate(Provider<StoryCommentsLoader> commentsLoaderProvider) {
        this.commentsLoaderProvider = commentsLoaderProvider;
    }

    public void setStoryDetailView(StoryDetailView view){
        this.storyDetailView = view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        StoryCommentsLoader commentsLoader = commentsLoaderProvider.get();
        detailStory = (DetailStory) args.getParcelable(StoryCommentsLoader.SELECTED_DETAIL_STORY);
        commentsLoader.setDetailStoryId(detailStory.getStoryId());
        return commentsLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Comment>> loader, final List<Comment> data) {
        Timber.d("Loader Finished");
        if(detailStory != null){
            detailStory.addComments(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Timber.d("Loader reset");
    }
}