package dev.bltucker.nanodegreecapstone.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.daos.CommentRefsDao;
import dev.bltucker.nanodegreecapstone.data.daos.StoryDao;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Story;
import io.reactivex.Observable;
import timber.log.Timber;

@ApplicationScope
public class ContentProviderBackedStoryRepository implements StoryRepository {

    @NonNull
    private final StoryDao storyDao;

    @NonNull
    private final CommentRefsDao commentRefsDao;

    @Inject
    public ContentProviderBackedStoryRepository(@NonNull StoryDao storyDao, @NonNull CommentRefsDao commentRefsDao) {
        this.storyDao = storyDao;
        this.commentRefsDao = commentRefsDao;
    }

    @Override
    public Observable<List<Story>> getAllStories() {
        return storyDao.getAllStories().toObservable();
    }

    @Override
    public void saveStories(Story[] stories) {
        storyDao.deleteAllStories();
        commentRefsDao.deleteAllCommentRefs();

        List<CommentReference> commentRefsContentValuesList = new ArrayList<>();

        for (int i = 0; i < stories.length; i++) {
            Story story = stories[i];
            commentRefsContentValuesList.addAll(getCommentRefList(story));
        }

        storyDao.saveStories(stories);

        CommentReference[] commentRefsContentValuesArray = new CommentReference[commentRefsContentValuesList.size()];
        commentRefsDao.saveAllRefs(commentRefsContentValuesList.toArray(commentRefsContentValuesArray));

    }

    private List<CommentReference> getCommentRefList(Story story) {
        Long[] commentIds = story.getCommentIds();
        List<CommentReference> contentValues = new ArrayList<>(commentIds.length);

        for (int i = 0; i < commentIds.length; i++) {
            CommentReference commentReference = new CommentReference(commentIds[i], story.getId(), i);
            contentValues.add(commentReference);
        }

        return contentValues;
    }
}
