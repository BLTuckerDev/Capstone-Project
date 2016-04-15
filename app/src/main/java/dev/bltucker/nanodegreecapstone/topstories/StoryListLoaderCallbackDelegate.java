package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Story;

class StoryListLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks {
    private final StoryListViewPresenter storyListViewPresenter;

    public StoryListLoaderCallbackDelegate(StoryListViewPresenter storyListViewPresenter) {
        this.storyListViewPresenter = storyListViewPresenter;
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        if (storyListViewPresenter.getStoryListView() != null) {
            storyListViewPresenter.getStoryListView().showLoadingView();
        }

        return storyListViewPresenter.getStoryListLoader();
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (storyListViewPresenter.getStoryListView() != null) {
            storyListViewPresenter.getReadingSession().setStories((List<Story>) data);
            storyListViewPresenter.getStoryListView().showStories((List<Story>) data);
            storyListViewPresenter.getStoryListView().hideLoadingView();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}