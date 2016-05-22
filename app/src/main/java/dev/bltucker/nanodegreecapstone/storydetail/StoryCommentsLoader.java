package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryCommentsLoader extends AsyncTaskLoader<List<Comment>> {

    static final String SELECTED_STORY_ID_BUNDLE_KEY = "selectedStoryBundleKey";


    public static final int STORY_COMMENT_LOADER = StoryCommentsLoader.class.hashCode();
    private final StoryRepository storyRepository;
    private long storyId;

    @Inject
    public StoryCommentsLoader(Context context, StoryRepository repository){
        super(context);
        storyRepository = repository;
        onContentChanged();
    }

    @Override
    public List<Comment> loadInBackground() {
        return storyRepository.getStoryComments(storyId).toBlocking().first();
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        }
    }

    public void setStoryId(long storyId) {
        this.storyId = storyId;
    }
}
