package dev.bltucker.nanodegreecapstone.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryListLoader extends AsyncTaskLoader<List<Story>> {

    public static final int STORY_LIST_LOADER = StoryListLoader.class.hashCode();

    private final StoryRepository storyRepository;

    @Inject
    public StoryListLoader(Context context, StoryRepository storyRepository) {
        super(context);
        this.storyRepository = storyRepository;
        onContentChanged();
    }

    @Override
    public List<Story> loadInBackground() {
        return storyRepository.getAllStories().toBlocking().first();
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        }
    }
}
