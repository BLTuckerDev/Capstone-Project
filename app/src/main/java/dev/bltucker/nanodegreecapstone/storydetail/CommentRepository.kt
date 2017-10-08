package dev.bltucker.nanodegreecapstone.storydetail

import javax.inject.Inject

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.models.Comment
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

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
