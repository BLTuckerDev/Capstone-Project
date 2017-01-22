package dev.bltucker.nanodegreecapstone.sync;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import dev.bltucker.nanodegreecapstone.data.DatabaseGenerator;
import dev.bltucker.nanodegreecapstone.data.StoryDatabase;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DeleteOrphanCommentsActionTest {

    private StoryDatabase storyDatabase;
    private SQLiteDatabase writableDatabase;
    private DeleteOrphanCommentsAction objectUnderTest;

    @Before
    public void setUp() throws Exception {
        storyDatabase = StoryDatabase.getInstance(InstrumentationRegistry.getContext());
        writableDatabase = storyDatabase.getWritableDatabase();
        objectUnderTest = new DeleteOrphanCommentsAction(storyDatabase);
    }

    @After
    public void tearDown(){
        InstrumentationRegistry.getContext().deleteDatabase(storyDatabase.getDatabaseName());
    }

    @Test
    public void testWithOrphanCommentsShouldDeleteJustTheOrphans() throws Exception {
        final long fakeTime = System.currentTimeMillis();
        final long validStoryId = 2000L;
        final long invalidStoryId = validStoryId + 10;
        final int rootDepthCommentDepth = 0;

        //create a story
        Story story = new Story(validStoryId, "Tester", 100L, fakeTime, "A Test Story", "https://google.com/", new Long[0]);
        ContentValues contentValues = Story.mapToContentValues(story);
        writableDatabase.insert(DatabaseGenerator.STORIES, null, contentValues);

        //create a comment linked to that story
        long validStoryCommentId = 101L;
        Comment validStoryComment = new Comment(validStoryCommentId, "Tester", "Valid", fakeTime, validStoryId, rootDepthCommentDepth);
        writableDatabase.insert(DatabaseGenerator.COMMENTS, null, Comment.mapToContentValues(validStoryComment));


        //create comment that is no longer linked to a story in the db
        long invalidStoryCommentId = 102L;
        Comment invalidStoryComment = new Comment(invalidStoryCommentId, "Tester", "Invalid", fakeTime, invalidStoryId, rootDepthCommentDepth);
        writableDatabase.insert(DatabaseGenerator.COMMENTS, null, Comment.mapToContentValues(invalidStoryComment));

        //create child comments of the invalid story's parent comment
        for(int i = 1; i < 4; i++){
            long invalidStoryCommentChildId = invalidStoryCommentId + i;
            Comment invalidStoryCommentChild = new Comment(invalidStoryCommentChildId, "tester", "invalid child", fakeTime, invalidStoryCommentChildId-1, rootDepthCommentDepth+i);
            writableDatabase.insert(DatabaseGenerator.COMMENTS, null, Comment.mapToContentValues(invalidStoryCommentChild));
        }
        
        Cursor beforeCursor = writableDatabase.rawQuery("SELECT * FROM " + DatabaseGenerator.COMMENTS, new String[0]);

        assertEquals(5, beforeCursor.getCount());

        objectUnderTest.call();

        Cursor afterCursor = writableDatabase.rawQuery("SELECT * FROM " + DatabaseGenerator.COMMENTS, new String[0]);

        assertEquals(1, afterCursor.getCount());
        afterCursor.moveToNext();
        assertEquals(validStoryCommentId, afterCursor.getLong(afterCursor.getColumnIndex(CommentColumns.COMMENT_ID)));

        beforeCursor.close();
        afterCursor.close();
    }

    @Test
    public void testWithNoOrphanCommentsShouldNotDeleteAnyComments(){
        final long fakeTime = System.currentTimeMillis();
        final long validStoryId = 2000L;
        final int rootDepthCommentDepth = 0;

        //create a story
        Story story = new Story(validStoryId, "Tester", 100L, fakeTime, "A Test Story", "https://google.com/", new Long[0]);
        ContentValues contentValues = Story.mapToContentValues(story);
        writableDatabase.insert(DatabaseGenerator.STORIES, null, contentValues);

        //create a comment linked to that story
        long validStoryCommentId = 101L;
        Comment validStoryComment = new Comment(validStoryCommentId, "Tester", "Valid", fakeTime, validStoryId, rootDepthCommentDepth);
        writableDatabase.insert(DatabaseGenerator.COMMENTS, null, Comment.mapToContentValues(validStoryComment));

        //create child comments
        for(int i = 1; i < 4; i++){
            long validStoryCommentChildId = validStoryCommentId + i;
            Comment validStoryChildComment = new Comment(validStoryCommentChildId, "tester", "invalid child", fakeTime, validStoryCommentChildId-1, rootDepthCommentDepth+i);
            writableDatabase.insert(DatabaseGenerator.COMMENTS, null, Comment.mapToContentValues(validStoryChildComment));
        }

        Cursor beforeCursor = writableDatabase.rawQuery("SELECT * FROM " + DatabaseGenerator.COMMENTS, new String[0]);

        assertEquals(4, beforeCursor.getCount());

        objectUnderTest.call();

        Cursor afterCursor = writableDatabase.rawQuery("SELECT * FROM " + DatabaseGenerator.COMMENTS, new String[0]);

        assertEquals(4, afterCursor.getCount());
        afterCursor.moveToNext();

        beforeCursor.close();
        afterCursor.close();
    }
}