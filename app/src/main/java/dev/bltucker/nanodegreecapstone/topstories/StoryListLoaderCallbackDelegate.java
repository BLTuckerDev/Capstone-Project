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

class StoryListLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks<List<Story>> {

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
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> newStories) {
        Timber.d("onLoadFinished");
        if(!newStories.isEmpty()){
            Timber.d("StoryListLoader finished, first story title in new data set is: %s", newStories.get(0).getTitle());
        } else {
            Timber.d("StoryListLoader finished, there are no stories");
        }

        readingSession.setLatestSyncStories(newStories);

        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
            if(storyListView != null){
                if(readingSession.hasStories()){
                    if(readingSession.isStoryListIsDirty()){
                        storyListView.showUpdatedStoriesNotification();
                    }
                } else {
                    readingSession.updateUserStoriesToLatestSync();
                    storyListView.showStories();
                }
            }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Timber.d("storyListLoaderCallback.onLoaderReset");
    }
}