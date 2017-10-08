package dev.bltucker.nanodegreecapstone.storydetail

import javax.inject.Inject

import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.models.Comment
import io.reactivex.Observable

@ApplicationScope
open class CommentRepository @Inject
constructor(private val commentsDao: CommentsDao) {

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
