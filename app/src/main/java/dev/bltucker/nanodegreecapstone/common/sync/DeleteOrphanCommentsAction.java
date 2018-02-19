package dev.bltucker.nanodegreecapstone.common.sync;

import dev.bltucker.nanodegreecapstone.common.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.common.models.Comment;
import io.reactivex.functions.Action;
import timber.log.Timber;

class DeleteOrphanCommentsAction implements Action {

    private final CommentsDao commentsDao;

    DeleteOrphanCommentsAction(CommentsDao commentsDao) {
        this.commentsDao = commentsDao;
    }

    @Override
    public void run() {
        Comment[] rootOrphanComments = commentsDao.getRootOrphanComments();
        int deletedCommentCount = 0;

        for (int i = 0; i < rootOrphanComments.length; i++) {
            Comment parentComment = rootOrphanComments[i];
            Comment[] childComments = commentsDao.getChildComments(parentComment.id);

            deletedCommentCount += commentsDao.deleteComments(childComments);
            deletedCommentCount += commentsDao.deleteComments(parentComment);
        }

        Timber.d("Comment Clean up job removed %d comments", deletedCommentCount);
    }
}
