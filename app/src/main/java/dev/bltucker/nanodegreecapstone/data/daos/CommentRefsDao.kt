package dev.bltucker.nanodegreecapstone.data.daos

import android.arch.persistence.room.*
import dev.bltucker.nanodegreecapstone.data.CommentReference

@Dao
interface CommentRefsDao {

    //default sort = READ_RANK
    @Query("SELECT * FROM commentRefs WHERE storyId = :storyId order by readRank asc")
    fun getCommentRefsForStoryId(storyId: Long): Array<CommentReference>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllRefs(commentRefs: Array<CommentReference>)

    @Query("DELETE FROM commentRefs")
    fun deleteAllCommentRefs()
}