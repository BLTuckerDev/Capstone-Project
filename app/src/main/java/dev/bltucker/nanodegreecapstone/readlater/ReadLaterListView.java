package dev.bltucker.nanodegreecapstone.readlater;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;

public interface ReadLaterListView {
    void showLoadingSpinner();

    void hideLoadingSpinner();

    void showStories(List<ReadLaterStory> data);

    void showEmptyView();

    void readStory(ReadLaterStory story);
}
