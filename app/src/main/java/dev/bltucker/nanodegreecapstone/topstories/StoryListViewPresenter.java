package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryListLoader;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StoryListViewPresenter implements LoaderManager.LoaderCallbacks<List<Story>> {

    private final StoryRepository storyRepository;
    //TODO make use of the reading session
    private final ReadingSession readingSession;
    private final EventBus eventBus;

    private StoryListView storyListView;
    private Subscription syncCompletedEventSubscription;

    private final Loader storyListLoader;

    @Inject
    public StoryListViewPresenter(StoryRepository storyRepository, ReadingSession readingSession, EventBus eventBus, StoryListLoader storyListLoader) {
        this.storyRepository = storyRepository;
        this.readingSession = readingSession;
        this.eventBus = eventBus;
        this.storyListLoader = storyListLoader;
    }


    public void onViewCreated(StoryListView view, LoaderManager loaderManager) {
        storyListView = view;
        if(readingSession.getStories().isEmpty()){
            loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, this).forceLoad();
        } else {
            view.showStories(readingSession.getStories());
        }
    }

    public void onViewRestored(StoryListView view, LoaderManager loaderManager) {
        storyListView = view;
        if(readingSession.getStories().isEmpty()){
            loaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, this).forceLoad();
        } else {
            view.showStories(readingSession.getStories());
        }
    }

    public void onViewResumed(StoryListView view) {
        storyListView = view;
        //TODO consider moiving this to create/restore
        //and remembering to update the view after it is resumed if an update occurred while it was
        //"paused"
        syncCompletedEventSubscription = eventBus.subscribeTo(SyncCompletedEvent.class)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Object o) {
                        handleOnSyncCompleteEvent();
                    }
                });
    }

    public void onViewPaused(StoryListView view) {
        storyListView = null;
        syncCompletedEventSubscription.unsubscribe();
    }

    private void handleOnSyncCompleteEvent() {
        //TODO restart the loader
    }

    public void onCommentsButtonClick(final Story selectedStory) {
        if(storyListView != null){
//TODO convert to a loader
            storyListView.showLoadingView();

            storyRepository.getStoryComments(selectedStory)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Comment>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            storyListView.hideLoadingView();
                        }

                        @Override
                        public void onNext(List<Comment> comments) {
                            readingSession.read(selectedStory, comments);
                            if(storyListView != null){
                                storyListView.hideLoadingView();
                                storyListView.showCommentsView();

                            }
                        }
                    });
        }
    }

    public void onReadStoryButtonClick(Story story) {
        if(storyListView != null){
            storyListView.showStoryPostUrl(story.getUrl());
        }
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        if(storyListView != null){
            storyListView.showLoadingView();
        }

        return storyListLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> data) {
        if(storyListView != null){
            readingSession.setStories(data);
            storyListView.showStories(data);
            storyListView.hideLoadingView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {   }
}
