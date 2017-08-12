package dev.bltucker.nanodegreecapstone.topstories;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.models.Story;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class StoryListLoader extends AsyncTaskLoader<List<Story>> {

    public static final int STORY_LIST_LOADER = StoryListLoader.class.hashCode();

    private final StoryRepository storyRepository;
    private final EventBus eventBus;
    private Disposable syncAdapterChangeSubscription;

    @Inject
    public StoryListLoader(Context context, StoryRepository storyRepository, EventBus eventBus) {
        super(context);
        this.storyRepository = storyRepository;
        this.eventBus = eventBus;
        onContentChanged();
    }

    @Override
    public List<Story> loadInBackground() {
        Timber.d("StoryListLoader loading in background");
        return storyRepository.getAllStories().blockingFirst();
    }

    @Override
    protected void onStartLoading() {
        subscribeToSyncEvents();

        if (takeContentChanged()) {
            forceLoad();
        }
    }


    @Override
    protected void onReset() {
        syncAdapterChangeSubscription.dispose();
        super.onReset();
    }


    private void subscribeToSyncEvents(){
        eventBus.subscribeTo(SyncCompletedEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error thrown after a sync complete event handler");
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        syncAdapterChangeSubscription = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        Timber.d("StoryListLoader onNext, setting content changed");
                        onContentChanged();
                    }
                });
    }
}
