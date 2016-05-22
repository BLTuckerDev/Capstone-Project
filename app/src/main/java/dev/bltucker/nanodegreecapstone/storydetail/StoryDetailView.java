package dev.bltucker.nanodegreecapstone.storydetail;

public interface StoryDetailView {
    void showStory();
    void showComments();
    void showStoryPostUrl();
    void showStorySaveConfirmation();
    void showCommentsLoadingSpinner();
    void hideCommentsLoadingSpinner();
    void showEmptyView();
    void showSelectAStoryPrompt();
}
