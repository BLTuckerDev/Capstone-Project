package dev.bltucker.nanodegreecapstone;

import android.os.Looper;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StoryListFragmentPresenter {

    private final StoryRepository storyRepository;
    private final ReadingSession readingSession;

    private StoryListView storyListView;

    @Inject
    public StoryListFragmentPresenter(StoryRepository storyRepository, ReadingSession readingSession) {
        this.storyRepository = storyRepository;
        this.readingSession = readingSession;
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
              public void onCompleted() {
              }

              @Override
              public void onError(Throwable e) {
              }

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
    }

    public void onViewPaused(StoryListView view) {
        storyListView = view;
    }
}
