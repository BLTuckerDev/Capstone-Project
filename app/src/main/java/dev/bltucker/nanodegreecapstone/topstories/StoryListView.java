package dev.bltucker.nanodegreecapstone.topstories;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

public interface StoryListView {
    void showStories();

    void showCommentsView();

    void showStoryPostUrl(String url);

    void showLoadingView();
    void hideLoadingView();
}
