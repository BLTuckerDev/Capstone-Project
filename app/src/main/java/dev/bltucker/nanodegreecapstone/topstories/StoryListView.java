package dev.bltucker.nanodegreecapstone.topstories;

public interface StoryListView {
    void showStories();

    void showStoryDetailView(int storyPosition);

    void showStoryPostUrl(String url);

    void showLoadingView();
    void hideLoadingView();
}
