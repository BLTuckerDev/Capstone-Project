package dev.bltucker.nanodegreecapstone.common.data.daos

import android.arch.persistence.room.*
import android.database.Cursor
import dev.bltucker.nanodegreecapstone.common.models.ReadLaterStory
import io.reactivex.Flowable

@Dao
interface ReadLaterStoryDao {

    @Query("SELECT * FROM readLaterStories order by _id asc")
    fun getAllReadLaterStories(): Flowable<List<ReadLaterStory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStory(readLaterStory: ReadLaterStory)

    @Delete
    fun deleteStory(readLaterStory: ReadLaterStory)

    @Query("SELECT * FROM readLaterStories order by _id asc")
    fun getAllReadLaterStoriesCursor(): Cursor

}