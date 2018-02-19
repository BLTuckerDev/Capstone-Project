package dev.bltucker.nanodegreecapstone.storydetail

import javax.inject.Inject

import dev.bltucker.nanodegreecapstone.common.data.daos.CommentsDao
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.common.models.Comment
import io.reactivex.Observable

@ApplicationScope
open class CommentRepository @Inject
constructor(private val commentsDao: CommentsDao) {

    fun getRootOrphanComments(): Array<Comment> {
        return commentsDao.getRootOrphanComments()
    }

    fun getChildComments(parentCommentId: Long): Array<Comment> {
        return commentsDao.getChildComments(parentCommentId)
    }

    fun deleteComments(vararg comments: Comment): Int {
        return commentsDao.deleteComments(*comments)
    }

    fun getCommentsForStoryId(storyId: Long): Observable<Array<Comment>> {
        return getLocalComments(storyId)
    }

    fun saveComment(comment: Comment) {
        commentsDao.save(comment)
    }

    private fun getLocalComments(storyId: Long): Observable<Array<Comment>> {
        return commentsDao.getStoryCommentsFlowable(storyId)
                .onBackpressureDrop()
                .toObservable()
    }
}
