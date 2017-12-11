package dev.bltucker.nanodegreecapstone.data.daos

import android.arch.persistence.room.*
import android.database.Cursor
import dev.bltucker.nanodegreecapstone.models.Story
import io.reactivex.Flowable

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStories(stories: Array<Story>)

    @Query("SELECT * FROM stories order by rank asc")
    fun getAllStories(): Flowable<List<Story>>
    //Room will automatically do the querying on a background thread, so we only need to worry about observeOn

    @Query("DELETE FROM stories")
    fun deleteAllStories()

    @Query("SELECT * FROM stories order by rank asc")
    fun getAllStoriesCursor(): Cursor
}