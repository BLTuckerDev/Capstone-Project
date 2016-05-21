package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import timber.log.Timber;

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
        Timber.d("StoryListLoaderCallbackDelegate.onCreateLoader");
        StoryListLoader storyListLoader = storyListLoaderProvider.get();
        Timber.d("Returning an instance of story list loader %d", storyListLoader.hashCode());
        return storyListLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Timber.d("onLoadFinished");
        final List<Story> newStories = (List<Story>)data;
        if(!newStories.isEmpty()){
            Timber.d("StoryListLoader finished, first story title in new data set is: %s", newStories.get(0).getTitle());
        }
        readingSession.setStories((List<Story>) data);
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                if(storyListView != null){
                    Timber.d("StoryListView is available to the loader. View is being updated");
                    storyListView.hideLoadingSpinner();
                    storyListView.showStories();
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Timber.d("storyListLoaderCallback.onLoaderReset");
    }
}