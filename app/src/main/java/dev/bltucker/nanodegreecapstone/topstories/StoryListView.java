package dev.bltucker.nanodegreecapstone.topstories;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Story;

public interface StoryListView {
    void showStories(List<Story> stories);

    void showStoryDetails(Story story);

    void showStoryPostUrl(String url);
}
