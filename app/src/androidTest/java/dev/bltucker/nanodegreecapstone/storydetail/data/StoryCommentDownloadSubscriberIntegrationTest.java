package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.database.Cursor;
import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.models.Comment;

public class StoryCommentDownloadSubscriberIntegrationTest extends AndroidTestCase {

    StoryCommentDownloadSubscriber objectUnderTest;

    @Override
    public void setUp(){
        mContext.getContentResolver().delete(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, null, null);
        objectUnderTest = new StoryCommentDownloadSubscriber(mContext.getContentResolver(), new EventBus());
    }

    @Override
    public void tearDown(){
        mContext.getContentResolver().delete(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, null, null);
    }

    @Test
    public void testSavingToContentResolver(){

        List<Comment> commentList = new ArrayList<>();

        String firstAuthorName = "Author Name";
        long firstParentId = 1000L;
        commentList.add(new Comment(1L, firstAuthorName, "Comment Text", System.currentTimeMillis(), firstParentId));

        String secondAuthorName = "Another Author Name";
        long secondParentId = 2000L;
        commentList.add(new Comment(2L, secondAuthorName, "More Comment Text", System.currentTimeMillis(), secondParentId));

        objectUnderTest.onNext(commentList);


        Cursor firstQuery = mContext.getContentResolver().query(SchematicContentProviderGenerator.CommentPaths.withStoryId(String.valueOf(firstParentId)),
                null,
                null,
                null,
                null);

        Cursor secondQuery = mContext.getContentResolver().query(SchematicContentProviderGenerator.CommentPaths.withStoryId(String.valueOf(secondParentId)),
                null,
                null,
                null,
                null);

        assertTrue(firstQuery.moveToFirst());
        assertTrue(secondQuery.moveToFirst());

        assertEquals(firstAuthorName, firstQuery.getString(firstQuery.getColumnIndex(CommentColumns.AUTHOR_NAME)));
        assertEquals(secondAuthorName, secondQuery.getString(firstQuery.getColumnIndex(CommentColumns.AUTHOR_NAME)));

    }


}
