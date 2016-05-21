package dev.bltucker.nanodegreecapstone.topstories;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

public class StoryListLoader extends AsyncTaskLoader<List<Story>> {

    public static final int STORY_LIST_LOADER = StoryListLoader.class.hashCode();

    private final StoryRepository storyRepository;
    private final EventBus eventBus;
    private Subscription syncAdapterChangeSubscription;

    @Inject
    public StoryListLoader(Context context, StoryRepository storyRepository, EventBus eventBus) {
        super(context);
        this.storyRepository = storyRepository;
        this.eventBus = eventBus;
        onContentChanged();
    }

    @Override
    public List<Story> loadInBackground() {
        return storyRepository.getAllStories().toBlocking().first();
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
        syncAdapterChangeSubscription.unsubscribe();
        super.onReset();
    }


    private void subscribeToSyncEvents(){
        syncAdapterChangeSubscription = eventBus.subscribeTo(SyncCompletedEvent.class)
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {   }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error thrown after a sync complete event handler");
                    }

                    @Override
                    public void onNext(Object o) {
                        onContentChanged();
                    }
                });
    }
}
