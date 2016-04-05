package dev.bltucker.nanodegreecapstone;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Story;

public interface StoryListView {
    void showStories(List<Story> stories);
}
