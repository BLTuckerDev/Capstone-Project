package dev.bltucker.nanodegreecapstone.data;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Comment;
import io.reactivex.Observable;

@ApplicationScope
public class CommentRepository {

    private final CommentsDao commentsDao;

    @Inject
    public CommentRepository(CommentsDao commentsDao){
        this.commentsDao = commentsDao;
    }

    public Observable<List<Comment>> getStoryComments(final long storyId) {
        return Observable.fromArray(commentsDao.getStoryComments(storyId)).toList().toObservable();
    }

    public void saveComment(Comment comment){
        commentsDao.save(comment);
    }
}
