package dev.bltucker.nanodegreecapstone.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.data.CommentRefsColumns;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public final class StorySyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT = "storySyncAccount";

    public static final String ACCOUNT_TYPE = "bltucker.dev";
    public static final int MAX_STORY_COUNT = 150;

    @Inject
    HackerNewsApiService apiService;

    @Inject
    StoryRepository storyRepository;

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

        Timber.d("Syncing....");

        apiService.getTopStoryIds()
              .concatMap(new Func1<List<Long>, Observable<List<Story>>>() {
                  @Override
                  public Observable<List<Story>> call(List<Long> storyIds) {
                      List<Story> storyList = new ArrayList<>(storyIds.size());
                      final int maxStories = Math.min(storyIds.size(), MAX_STORY_COUNT);

                      for (int i = 0; i < maxStories; i++) {
                          Story story = apiService.getStory(storyIds.get(i)).toBlocking().first();
                          story.setStoryRank(i);
                          storyList.add(story);
                      }

                      return Observable.just(storyList);
                  }
              })
              .subscribe(new Subscriber<List<Story>>() {
                  @Override
                  public void onCompleted() {  }

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
}
