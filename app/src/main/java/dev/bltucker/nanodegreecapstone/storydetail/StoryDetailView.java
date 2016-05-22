package dev.bltucker.nanodegreecapstone.storydetail;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;

public interface StoryDetailView {
    void showStory();
    void showComments(List<Comment> data);
    void showStoryPostUrl();
    void showStorySaveConfirmation();
    void showCommentsLoadingSpinner();
    void hideCommentsLoadingSpinner();
    void showEmptyView();
    void showSelectAStoryPrompt();
}
