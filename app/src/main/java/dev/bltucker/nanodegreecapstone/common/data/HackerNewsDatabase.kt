package dev.bltucker.nanodegreecapstone.common.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import dev.bltucker.nanodegreecapstone.common.data.daos.CommentRefsDao
import dev.bltucker.nanodegreecapstone.common.data.daos.CommentsDao
import dev.bltucker.nanodegreecapstone.common.data.daos.ReadLaterStoryDao
import dev.bltucker.nanodegreecapstone.common.data.daos.StoryDao

import dev.bltucker.nanodegreecapstone.common.models.Comment
import dev.bltucker.nanodegreecapstone.common.models.ReadLaterStory
import dev.bltucker.nanodegreecapstone.common.models.Story

@Database(entities = arrayOf(Story::class, CommentReference::class, ReadLaterStory::class, Comment::class), version = 3)
abstract class HackerNewsDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao

    abstract fun commentRefsDao(): CommentRefsDao

    abstract fun readLaterStoryDao(): ReadLaterStoryDao

    abstract fun commentsDao(): CommentsDao
}
