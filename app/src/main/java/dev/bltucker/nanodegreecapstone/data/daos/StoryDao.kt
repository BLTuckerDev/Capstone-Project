package dev.bltucker.nanodegreecapstone.data.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.bltucker.nanodegreecapstone.models.Story
import io.reactivex.Flowable

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStories(stories : Array<Story>) : Unit

    @Query("SELECT * FROM stories order by rank asc")
    fun getAllStories() : Flowable<List<Story>>
    //Room will automatically do the querying on a background thread, so we only need to worry about observeOn
}