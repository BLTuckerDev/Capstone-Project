package dev.bltucker.nanodegreecapstone.storydetail.data;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.CommentRepository;
import io.reactivex.Observable;

@ApplicationScope
public final class StoryCommentsObservableFactory {

    private final CommentRepository commentRepository;

    @Inject
    public StoryCommentsObservableFactory(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Observable<Comment> get(final long storyId, final long[] commentIds) {
        return commentRepository.getCommentsForStoryId(storyId).concatMap(Observable::fromArray);
    }

}
