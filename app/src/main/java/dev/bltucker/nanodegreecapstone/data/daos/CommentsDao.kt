package dev.bltucker.nanodegreecapstone.data.daos

import android.arch.persistence.room.*
import dev.bltucker.nanodegreecapstone.models.Comment
import io.reactivex.Flowable

@Dao
interface CommentsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(comments: List<Comment>)

    @Query("SELECT * FROM comments t1 where parentId NOT IN (SELECT _id FROM stories) AND depth = 0")
    fun getRootOrphanComments(): Array<Comment>

    @Query("SELECT * FROM comments where storyId = :storyId")
    fun getStoryComments(storyId: Long): Array<Comment>

    @Query("SELECT * FROM comments where storyId = :storyId")
    fun getStoryCommentsFlowable(storyId: Long): Flowable<Array<Comment>>

    @Query("SELECT * from comments where parentId = :parentCommentId")
    fun getChildComments(parentCommentId: Long): Array<Comment>

    @Delete
    fun deleteComments(vararg comments: Comment): Int

}