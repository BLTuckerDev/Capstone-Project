package dev.bltucker.nanodegreecapstone.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import timber.log.Timber;

public class ReadingSession {

    private final List<Story> userReadingList;
    private boolean storyListIsDirty = false;

    private final List<Story> latestSyncStories;

    @Inject
    public ReadingSession(@StoryMax int maximumStoryCount){
        userReadingList = new ArrayList<>(maximumStoryCount);
        latestSyncStories = new ArrayList<>(maximumStoryCount);
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
