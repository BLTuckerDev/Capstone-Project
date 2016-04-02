package dev.bltucker.nanodegreecapstone.data;


import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StoryProviderTests extends AndroidTestCase {

    @Override
    public void setUp(){
        mContext.getContentResolver().delete(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, null, null);
        mContext.getContentResolver().delete(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS, null, null);
    }

    @Override
    public void tearDown(){
        mContext.getContentResolver().delete(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, null, null);
        mContext.getContentResolver().delete(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS, null, null);

    }

    public void testProviderRegistration(){
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), StoryProvider.class.getName());

        try{
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals(providerInfo.authority, SchematicContentProviderGenerator.AUTHORITY);
        } catch(Exception ex){
            fail();
        }
    }

    public void testStoryInsert(){
        Story testStory = new Story(Long.MAX_VALUE, "Brett Tucker", 100, new Date().getTime(), "A Story Title", "https://google.com/", new long[]{1,2,3,4});

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        mContext.getContentResolver().registerContentObserver(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES,
                true,
                TestContentObserver.createInstance(countDownLatch, mContext));

        ContentValues cv = Story.mapToContentValues(testStory);

        Uri insertedUri = mContext.getContentResolver().insert(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, cv);

        try{
            countDownLatch.await(15_000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex){
            fail();
        }

        assertNotNull(insertedUri);
        
        Cursor storyCursor = mContext.getContentResolver().query(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES,
                null,
                null,
                null,
                null);

        assertTrue(storyCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = cv.valueSet();

        for(Map.Entry<String, Object> values : valueSet){
            String columnName = values.getKey();
            int cursorColumnIndex = storyCursor.getColumnIndex(columnName);

            switch (columnName){
                case StoryColumns._ID:
                    assertEquals(values.getValue(), storyCursor.getLong(cursorColumnIndex));
                    break;

                case StoryColumns.AUTHOR_NAME:
                    assertEquals(values.getValue(), storyCursor.getString(cursorColumnIndex));
                    break;

                case StoryColumns.SCORE:
                    assertEquals(values.getValue(), storyCursor.getLong(cursorColumnIndex) );
                    break;

                case StoryColumns.TITLE:
                    assertEquals(values.getValue(), storyCursor.getString(cursorColumnIndex));
                    break;

                case StoryColumns.UNIX_TIME:
                    assertEquals(values.getValue(), storyCursor.getLong(cursorColumnIndex));
                    break;

                case StoryColumns.URL:
                    assertEquals(values.getValue(), storyCursor.getString(cursorColumnIndex));
                    break;
            }
        }

        storyCursor.close();
    }

    public void testCommentInsert(){
        final long storyId = Long.MAX_VALUE;
        final Long[] commentIds = new Long[]{1L,2L,3L,4L,5L};
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        mContext.getContentResolver().registerContentObserver(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS,
                true,
                TestContentObserver.createInstance(countDownLatch, mContext));

        final ContentValues[] contentValues = new ContentValues[commentIds.length];
        for(int i = 0; i < commentIds.length; i++){
            ContentValues cv = new ContentValues();
            cv.put(CommentRefsColumns._ID, commentIds[i]);
            cv.put(CommentRefsColumns.STORY_ID, storyId);
            contentValues[i] = cv;
        }

        int insertCount = mContext.getContentResolver().bulkInsert(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS, contentValues);

        try{
            countDownLatch.await(15_000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex){
            fail();
        }

        assertEquals(commentIds.length, insertCount);

        Cursor commentsCursor = mContext.getContentResolver().query(SchematicContentProviderGenerator.CommentRefs.withStoryId(String.valueOf(storyId)),
                null,
                null,
                null,
                null);

        assertTrue(commentsCursor.moveToFirst());
        assertEquals(commentIds.length, commentsCursor.getCount());

        commentsCursor.close();
    }

}

