package dev.bltucker.nanodegreecapstone.topstories;

import java.util.List;

import javax.inject.Inject;

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

public class StoryListViewPresenter {

    private final StoryRepository storyRepository;
    //TODO make use of the reading session
    private final ReadingSession readingSession;
    private final EventBus eventBus;

    private StoryListView storyListView;
    private Subscription syncCompletedEventSubscription;

    @Inject
    public StoryListViewPresenter(StoryRepository storyRepository, ReadingSession readingSession, EventBus eventBus) {
        this.storyRepository = storyRepository;
        this.readingSession = readingSession;
        this.eventBus = eventBus;
    }


    public void onViewCreated(StoryListView view) {
        storyListView = view;
        if(readingSession.getStories().isEmpty()){
            loadStories();
        } else {
            view.showStories(readingSession.getStories());
        }
    }

    public void onViewRestored(StoryListView view) {
        storyListView = view;
        if(readingSession.getStories().isEmpty()){
            loadStories();
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
        loadStories();
    }

    private void loadStories() {
        storyRepository.getAllStories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Story>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Story> stories) {
                        readingSession.setStories(stories);
                        storyListView.showStories(stories);
                    }
                });
    }

    private void loadComments(final Story selectedStory){
        storyRepository.getStoryComments(selectedStory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Comment>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(List<Comment> comments) {
                        readingSession.read(selectedStory, comments);
                    }
                });
    }

    public void onCommentsButtonClick(Story story) {
        if(storyListView != null){
            //TODO first load the comments and place the story in the reading session
            storyListView.showStoryDetails(story);
        }
    }

    public void onReadStoryButtonClick(Story story) {
        if(storyListView != null){
            storyListView.showStoryPostUrl(story.getUrl());
        }
    }
}
