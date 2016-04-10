package dev.bltucker.nanodegreecapstone.storydetail;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public interface StoryDetailView {
    void showStory(Story story);
    void showComments(List<Comment> commentList);

    void showStoryPostUrl(String url);
}
