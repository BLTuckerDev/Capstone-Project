package dev.bltucker.nanodegreecapstone.data.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.bltucker.nanodegreecapstone.models.Comment

@Dao
interface CommentsDao {

    @Insert
    fun save(comment: Comment)

    @Insert
    fun saveAll(comments: List<Comment>)

    @Query("SELECT * FROM comments t1 where parentId NOT IN (SELECT _id FROM stories) AND depth = 0")
    fun getRootOrphanComments() : Array<Comment>

    @Query("SELECT * from comments where parentId = :parentCommentId")
    fun getChildComments(parentCommentId : Long) : Array<Comment>

    @Delete
    fun deleteComments(vararg comments : Comment) : Int

}