package dev.bltucker.nanodegreecapstone.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

@ApplicationScope
public class ReadingSession {

    private final List<Story> userReadingList;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private Disposable syncAdapterChangeSubscription;

    private boolean storyListIsDirty = false;

    private final List<Story> latestSyncStories;

    @Inject
    public ReadingSession(@StoryMax int maximumStoryCount, EventBus eventBus){
        userReadingList = new ArrayList<>(maximumStoryCount);
        latestSyncStories = new ArrayList<>(maximumStoryCount);

        eventBus.subscribeTo(SyncCompletedEvent.class)
                .subscribe(new Observer<Object>() {
                    @Override
                    @SuppressWarnings("squid:S1186")
                    public void onComplete() {   }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error thrown after a sync complete event handler");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        syncAdapterChangeSubscription = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        storyListIsDirty = true;
                    }
                });
    }

    public boolean hasStories(){
        return !userReadingList.isEmpty();
    }

    public boolean isStoryListIsDirty() {
        return storyListIsDirty;
    }

    @Nullable
    public Story getStory(int position) {
        if(position < 0 || position >= userReadingList.size()){
            return null;
        }
        return userReadingList.get(position);
    }

    public int storyCount() {
        return userReadingList.size();
    }

    public void setLatestSyncStories(List<Story> newStories) {
        latestSyncStories.clear();
        latestSyncStories.addAll(newStories);
        storyListIsDirty = !latestSyncStories.equals(userReadingList);
    }

    public void updateUserStoriesToLatestSync() {
        Timber.d("ReadingSessions stories are being updated");
        storyListIsDirty = false;
        userReadingList.clear();
        userReadingList.addAll(latestSyncStories);
    }
}
