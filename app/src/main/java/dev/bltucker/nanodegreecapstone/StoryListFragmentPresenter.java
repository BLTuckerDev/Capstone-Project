package dev.bltucker.nanodegreecapstone;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StoryListFragmentPresenter {

    private final StoryRepository storyRepository;
    private final ReadingSession readingSession;
    private final EventBus eventBus;

    private StoryListView storyListView;
    private Subscription syncCompletedEventSubscription;

    @Inject
    public StoryListFragmentPresenter(StoryRepository storyRepository, ReadingSession readingSession, EventBus eventBus) {
        this.storyRepository = storyRepository;
        this.readingSession = readingSession;
        this.eventBus = eventBus;
    }


    public void onViewCreated(StoryListView view) {
        storyListView = view;
//TODO move this to the repository, make it so the repo just returns observables
        Observable.create(new Observable.OnSubscribe<List<Story>>() {
            @Override
            public void call(Subscriber<? super List<Story>> subscriber) {
                subscriber.onNext(storyRepository.getAllStories());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<List<Story>>() {
              @Override
              public void onCompleted() {     }

              @Override
              public void onError(Throwable e) {     }

              @Override
              public void onNext(List<Story> stories) {
                  storyListView.showStories(stories);
              }
          });
    }

    public void onViewRestored(StoryListView view) {
        storyListView = view;
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
                        onSyncCompletedEvent();
                    }
                });
    }

    public void onViewPaused(StoryListView view) {
        storyListView = view;
        syncCompletedEventSubscription.unsubscribe();
    }

    private void onSyncCompletedEvent(){
        //TODO handle sync complete
    }
}
