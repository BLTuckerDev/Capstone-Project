package dev.bltucker.nanodegreecapstone.common.sync

import dev.bltucker.nanodegreecapstone.storydetail.CommentRepository
import io.reactivex.functions.Action
import timber.log.Timber
import javax.inject.Inject

class DeleteOrphanCommentsAction @Inject constructor(private val commentsDao: CommentRepository) : Action {

    override fun run() {
        val rootOrphanComments = commentsDao.getRootOrphanComments()
        var deletedCommentCount = 0

        for (i in rootOrphanComments.indices) {
            val parentComment = rootOrphanComments[i]
            val childComments = commentsDao.getChildComments(parentComment.id)

            deletedCommentCount += commentsDao.deleteComments(*childComments)
            deletedCommentCount += commentsDao.deleteComments(parentComment)
        }

        Timber.d("Comment Clean up job removed %d comments", deletedCommentCount)
    }
}
