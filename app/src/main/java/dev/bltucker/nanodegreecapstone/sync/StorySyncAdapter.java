package dev.bltucker.nanodegreecapstone.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.events.SyncStatusObserver;
import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public final class StorySyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT = "storySyncAccount";

    public static final String ACCOUNT_TYPE = "bltucker.dev";

    public static final String SYNC_COMPLETED_ACTION ="dev.bltucker.nanodegreecapstone.SYNC_COMPLETED";

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

    public StorySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
    }

    public StorySyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        observer.setSyncInProgress(true);
        Timber.d("Syncing....");
        final long startTime = System.currentTimeMillis();

        apiService.getTopStoryIds()
              .concatMap(new Func1<List<Long>, Observable<List<Story>>>() {
                  @Override
                  public Observable<List<Story>> call(List<Long> storyIds) {
                      List<Story> storyList = new ArrayList<>(storyIds.size());
                      final int maxStories = Math.min(storyIds.size(), maximumStoryCount);

                      for (int i = 0; i < maxStories; i++) {
                          Story story = apiService.getStory(storyIds.get(i)).toBlocking().first();
                          story.setStoryRank(i);
                          storyList.add(story);
                      }

                      return Observable.just(storyList);
                  }
              })
                .subscribeOn(Schedulers.io())
              .subscribe(new Subscriber<List<Story>>() {
                  @Override
                  public void onCompleted() {
                      final long stopTime = System.currentTimeMillis();
                      Timber.d("Sync completed in %d milliseconds", stopTime - startTime);
                      //notify the application
                      observer.setSyncInProgress(false);
                      eventBus.publish(new SyncCompletedEvent());
                      notifyWidgets();
                  }

                  @Override
                  public void onError(Throwable e) {
                      Timber.e(e, "Error downloading top stories");
                  }

                  @Override
                  public void onNext(List<Story> stories) {
                      storyRepository.saveStories(stories);
                  }
              });
    }

    private void notifyWidgets() {
        Intent syncCompletedIntent = new Intent();
        syncCompletedIntent.setPackage(getContext().getPackageName());
        syncCompletedIntent.setAction(SYNC_COMPLETED_ACTION);
        getContext().sendBroadcast(syncCompletedIntent);
    }
}
