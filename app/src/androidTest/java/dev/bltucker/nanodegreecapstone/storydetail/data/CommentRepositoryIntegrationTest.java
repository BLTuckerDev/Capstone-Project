package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.database.Cursor;
import android.test.AndroidTestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.models.Comment;

public class CommentRepositoryIntegrationTest extends AndroidTestCase {

    CommentRepository objectUnderTest;

    @Override
    public void setUp(){
        mContext.getContentResolver().delete(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, null, null);
        objectUnderTest = new CommentRepository(mContext.getContentResolver());
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
        commentList.add(new Comment(1L, 1L, firstAuthorName, "Comment Text", System.currentTimeMillis(), firstParentId, 1));

        String secondAuthorName = "Another Author Name";
        long secondParentId = 2000L;
        commentList.add(new Comment(1L, 2L, secondAuthorName, "More Comment Text", System.currentTimeMillis(), secondParentId, 1));

        objectUnderTest.saveComments(commentList);


        Cursor firstQuery = mContext.getContentResolver().query(SchematicContentProviderGenerator.CommentPaths.withParentId(String.valueOf(firstParentId)),
                null,
                null,
                null,
                null);

        Cursor secondQuery = mContext.getContentResolver().query(SchematicContentProviderGenerator.CommentPaths.withParentId(String.valueOf(secondParentId)),
                null,
                null,
                null,
                null);

        assertTrue(firstQuery.moveToFirst());
        assertTrue(secondQuery.moveToFirst());

        assertEquals(firstAuthorName, firstQuery.getString(firstQuery.getColumnIndex(CommentColumns.AUTHOR_NAME)));
        assertEquals(secondAuthorName, secondQuery.getString(firstQuery.getColumnIndex(CommentColumns.AUTHOR_NAME)));


        firstQuery.close();
        secondQuery.close();
    }


}
