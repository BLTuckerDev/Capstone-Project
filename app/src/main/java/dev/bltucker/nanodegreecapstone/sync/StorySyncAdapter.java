package dev.bltucker.nanodegreecapstone.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.events.SyncStartedEvent;
import dev.bltucker.nanodegreecapstone.events.SyncStatusObserver;
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.StoryMax;
import dev.bltucker.nanodegreecapstone.models.Story;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class StorySyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT = "storySyncAccount";

    public static final String ACCOUNT_TYPE = "bltucker.dev";

    public static final String SYNC_COMPLETED_ACTION = "dev.bltucker.nanodegreecapstone.SYNC_COMPLETED";

    @Inject
    SyncStatusObserver observer;

    @Inject
    HackerNewsApiService apiService;

    @Inject
    StoryRepository storyRepository;

    @Inject
    EventBus eventBus;

    @Inject
    @StoryMax
    int maximumStoryCount;

    @Inject
    @IO
    Scheduler ioScheduler;

    public StorySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        DaggerInjector.getApplicationComponent().inject(this);
    }

    public StorySyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        DaggerInjector.getApplicationComponent().inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        observer.setSyncInProgress(true);
        Timber.d("Syncing....");
        eventBus.publish(new SyncStartedEvent());
        final long startTime = System.currentTimeMillis();

        apiService.getTopStoryIds()
                .toObservable()
                .concatMap(storyIdList -> {
                    final int storyArrayLength = Math.min(storyIdList.size(), maximumStoryCount);
                    Story[] stories = new Story[storyArrayLength];

                    for (int i = 0; i < storyArrayLength; i++) {
                        Story story = apiService.getStory(storyIdList.get(i)).blockingGet();
                        story.setStoryRank(i);
                        stories[i] = story;
                    }

                    return Observable.just(stories);
                })
                .subscribeOn(ioScheduler)
                .subscribe(storyRepository::saveStories,
                        e -> {
                    Timber.e(e, "Error downloading top stories");
                    //TODO styorSyncErrorEvent and handle appropriately
                    observer.setSyncInProgress(false);
                    eventBus.publish(new SyncCompletedEvent());
                }, () -> {
                    final long stopTime = System.currentTimeMillis();
                    Timber.d("Sync completed in %d milliseconds", stopTime - startTime);
                    //notify the application
                    observer.setSyncInProgress(false);
                    eventBus.publish(new SyncCompletedEvent());
                    notifyWidgets();
                });
    }

    private void notifyWidgets() {
        Intent syncCompletedIntent = new Intent();
        syncCompletedIntent.setPackage(getContext().getPackageName());
        syncCompletedIntent.setAction(SYNC_COMPLETED_ACTION);
        getContext().sendBroadcast(syncCompletedIntent);
    }
}
