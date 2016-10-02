package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.models.Comment;
import timber.log.Timber;

public class StoryCommentsLoader extends AsyncTaskLoader<List<Comment>> {

    static final String SELECTED_DETAIL_STORY = "selectedStoryBundleKey";

    static final int STORY_COMMENT_LOADER = StoryCommentsLoader.class.hashCode();

    private long detailStoryId;
    private final CommentRepository commentRepository;
    private ForceLoadContentObserver myContentObserver;

    @Inject
    public StoryCommentsLoader(Context context, CommentRepository repository){
        super(context);
        commentRepository = repository;
        onContentChanged();
    }

    @Override
    public List<Comment> loadInBackground() {
        Timber.d("StoryCommentsLoader.loadInBackground");
        List<Comment> comments = commentRepository.getStoryComments(detailStoryId).toBlocking().first();
        Timber.d("StoryCommentsLoader loaded %d comments", comments.size());
        return comments;
    }

    @Override
    protected void onStartLoading() {
        myContentObserver = new ForceLoadContentObserver();
        getContext().getContentResolver().registerContentObserver(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, true, myContentObserver);

        if (takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        getContext().getContentResolver().unregisterContentObserver(myContentObserver);
        super.onReset();
    }

    public void setDetailStoryId(long detailStoryId) {
        this.detailStoryId = detailStoryId;
        onContentChanged();
    }
}
