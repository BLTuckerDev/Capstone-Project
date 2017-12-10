package dev.bltucker.nanodegreecapstone.topstories;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Story;

class StoryDiffUtilCallback extends DiffUtil.Callback {

    @NonNull
    private final List<Story> stories;

    @NonNull
    private final List<Story> updatedStories;

    StoryDiffUtilCallback(@NonNull List<Story> stories,
                          @NonNull List<Story> updatedStories) {
        this.stories = stories;
        this.updatedStories = updatedStories;
    }

    @Override
    public int getOldListSize() {
        return stories.size();
    }

    @Override
    public int getNewListSize() {
        return updatedStories.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Story oldStory = stories.get(oldItemPosition);
        Story newStory = updatedStories.get(newItemPosition);

        return oldStory.id == newStory.id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Story oldStory = stories.get(oldItemPosition);
        Story newStory = updatedStories.get(newItemPosition);

        boolean titlesAreEqual = oldStory.title != null && newStory.title != null &&
                oldStory.title.equals(newStory.title);

        boolean urlsAreEqual = oldStory.url != null && newStory.url != null &&
                oldStory.url.equals(newStory.url);

        return titlesAreEqual && urlsAreEqual;
    }
}
