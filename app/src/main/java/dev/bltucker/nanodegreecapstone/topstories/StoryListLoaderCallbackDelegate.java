package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.data.StoryListLoader;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

class StoryListLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks {

    private final ReadingSession readingSession;
    private final Provider<StoryListLoader> storyListLoaderProvider;
    private StoryListView storyListView;

    public StoryListLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryListLoader> loaderProvider) {
        this.readingSession = readingSession;
        this.storyListLoaderProvider = loaderProvider;
    }

    public void setStoryListView(StoryListView view){
        storyListView = view;
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        if (storyListView != null) {
            storyListView.showLoadingView();
        }

        return storyListLoaderProvider.get();
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (storyListView != null) {
            readingSession.setStories((List<Story>) data);
            storyListView.showStories((List<Story>) data);
            storyListView.hideLoadingView();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}