package dev.bltucker.nanodegreecapstone.common.data.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

class Version2to3(startVersion: Int, endVersion: Int) : Migration(startVersion, endVersion) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.beginTransaction()

        val alterTableStatement = "ALTER TABLE comments RENAME TO old_comments;"

        val createTableStatement: String = "CREATE TABLE `comments` " +
                "(`_id` INTEGER NOT NULL," +
                " `commentId` INTEGER NOT NULL UNIQUE," +
                " `storyId` INTEGER NOT NULL," +
                " `authorName` TEXT," +
                " `commentText` TEXT," +
                " `unixPostTime` INTEGER NOT NULL," +
                " `parentId` INTEGER NOT NULL," +
                " `depth` INTEGER NOT NULL," +
                " PRIMARY KEY(`_id`));"

        val copyValuesStatement = "INSERT INTO comments(commentId, storyId, authorName, commentText, unixPostTime, parentId, depth)" +
                " SELECT _id, storyId, authorName, commentText, unixPostTime, parentId, depth FROM old_comments;"

        val dropOldIndex = "DROP INDEX index_comments_storyid;"
        val dropOldTableStatement = "DROP TABLE old_comments;"
        val createNewIndex = "CREATE INDEX `index_comments_storyId` ON `comments` (`storyId`);"


        database.execSQL(alterTableStatement)
        database.execSQL(createTableStatement)
        database.execSQL(copyValuesStatement)
        database.execSQL(dropOldIndex)
        database.execSQL(dropOldTableStatement)
        database.execSQL(createNewIndex)

        database.endTransaction()

    }

}