package dev.bltucker.nanodegreecapstone.topstories;

import dev.bltucker.nanodegreecapstone.models.Story;

public interface StoryListView {
    void showStories();
    void showStoryDetailView(Story story);
    void showStoryPostUrl(String url);
    void showLoadingSpinner();
    void hideLoadingSpinner();

    void stopRefreshing();

    void showUpdatedStoriesNotification();
}
