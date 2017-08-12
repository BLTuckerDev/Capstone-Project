package dev.bltucker.nanodegreecapstone.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.daos.CommentRefsDao;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Comment;
import io.reactivex.Observable;
import timber.log.Timber;

@ApplicationScope
public class CommentRepository {

    private final CommentRefsDao commentRefsDao;
    private final CommentsDao commentsDao;

    @Inject
    public CommentRepository(CommentRefsDao commentRefsDao, CommentsDao commentsDao){
        this.commentRefsDao = commentRefsDao;
        this.commentsDao = commentsDao;
    }

    public Observable<List<Comment>> getStoryComments(final long storyId) {
        return getStoryCommentsByStoryId(storyId);
    }

    Long[] getCommentIds(long storyId) {
        CommentReference[] commentRefs = commentRefsDao.getCommentRefsForStoryId(storyId);
        Long[] commentIds = new Long[commentRefs.length];

        for (int i = 0; i < commentRefs.length; i++) {
            commentIds[i] = commentRefs[i].id;
        }

        return commentIds;
    }

    public void saveComments(List<Comment> comments) {
        Timber.d("Saving %d comments", comments.size());
        commentsDao.saveAll(comments);
    }

    public void saveComment(Comment comment){
        Timber.d("Saving single comment");
        commentsDao.save(comment);
    }

    private Observable<List<Comment>> getStoryCommentsByStoryId(final long storyId){
        Timber.d("Getting comments for story with id %d", storyId);
        Comment[] rootStoryComments = commentsDao.getChildComments(storyId);
        List<Comment> commentList = new ArrayList<>(Arrays.asList(rootStoryComments));
        return Observable.just(commentList);
    }
}
