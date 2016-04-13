package dev.bltucker.nanodegreecapstone.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryListLoader extends AsyncTaskLoader<List<Story>> {

    public static final int STORY_LIST_LOADER = 1;

    private final StoryRepository storyRepository;

    @Inject
    public StoryListLoader(Context context, StoryRepository storyRepository) {
        super(context);
        this.storyRepository = storyRepository;
    }


    @Override
    public List<Story> loadInBackground() {
        return storyRepository.getAllStories().toBlocking().first();
    }
}
