package dev.bltucker.nanodegreecapstone.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import dev.bltucker.nanodegreecapstone.models.Story;

@Database(
        entities = {Story.class,
                CommentReference.class,
                ReadLaterStory.class,
                Comment.class
        }, version = 2)
public abstract class HackerNewsDatabase extends RoomDatabase {
}
