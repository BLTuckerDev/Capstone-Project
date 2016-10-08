package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.DetailStory;
import timber.log.Timber;

public class StoryCommentLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks<List<Comment>> {

    private final Provider<StoryCommentsLoader> commentsLoaderProvider;

    @Nullable
    private DetailStory detailStory;

    public StoryCommentLoaderCallbackDelegate(Provider<StoryCommentsLoader> commentsLoaderProvider) {
        this.commentsLoaderProvider = commentsLoaderProvider;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Timber.d("onCreateLoader");
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

    public void setDetailStory(@Nullable DetailStory detailStory) {
        this.detailStory = detailStory;
    }
}