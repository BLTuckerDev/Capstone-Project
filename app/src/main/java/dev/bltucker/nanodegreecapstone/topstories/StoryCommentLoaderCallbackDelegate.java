package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

class StoryCommentLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks {
    private final StoryListViewPresenter storyListViewPresenter;

    public StoryCommentLoaderCallbackDelegate(StoryListViewPresenter storyListViewPresenter) {
        this.storyListViewPresenter = storyListViewPresenter;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        storyListViewPresenter.getCommentsLoader().setStory((Story) args.getParcelable(StoryListViewPresenter.SELECTED_STORY_BUNDLE_KEY));
        return storyListViewPresenter.getCommentsLoader();
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        storyListViewPresenter.getReadingSession().read(storyListViewPresenter.getCommentsLoader().getStory(), (List<Comment>) data);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (storyListViewPresenter.getStoryListView() != null) {
                    storyListViewPresenter.getStoryListView().hideLoadingView();
                    storyListViewPresenter.getStoryListView().showCommentsView();
                }
            }
        });

    }

    @Override
    public void onLoaderReset(Loader loader) { }
}