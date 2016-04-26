package dev.bltucker.nanodegreecapstone.storydetail;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public interface StoryDetailView {
    void showStory();
    void showComments();

    void showStoryPostUrl(String url);
}
