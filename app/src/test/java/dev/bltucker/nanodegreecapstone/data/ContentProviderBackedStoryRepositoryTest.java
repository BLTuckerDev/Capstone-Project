package dev.bltucker.nanodegreecapstone.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Date;
import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Story;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ContentResolver.class, Uri.class})
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class ContentProviderBackedStoryRepositoryTest {

    ContentResolver mockContentResolver;

    CommentRepository mockCommentRepository;

    ContentProviderBackedStoryRepository objectUnderTest;

    @Before
    public void setup(){
        mockContentResolver = mock(ContentResolver.class);
        mockCommentRepository = mock(CommentRepository.class);
        objectUnderTest = new ContentProviderBackedStoryRepository(mockContentResolver, mockCommentRepository);
    }

    @Test
    public void testGetAllStories() throws Exception {
        Cursor mockCursor = mock(Cursor.class);
        Cursor mockCommentCursor = mock(Cursor.class);
        Uri mockUri = mock(Uri.class);

        mockStatic(Uri.class);
        when(Uri.parse(anyString())).thenReturn(mockUri);
        when(mockContentResolver.query(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES,
                null,
                null,
                null,
                null))
                .thenReturn(mockCursor);
        mockCursorCalls(mockCursor);

        when(mockContentResolver.query(SchematicContentProviderGenerator.CommentRefs.withStoryId(String.valueOf(1L)), null, null, null, null))
                .thenReturn(mockCommentCursor);

        when(mockCommentCursor.getCount()).thenReturn(1);
        when(mockCommentCursor.getColumnIndex(CommentRefsColumns._ID)).thenReturn(0);
        when(mockCommentCursor.getLong(0)).thenReturn(1L);
        when(mockCommentCursor.moveToNext()).thenReturn(true, false);

        List<Story> storyList = objectUnderTest.getAllStories().toBlocking().first();

        assertEquals(1, storyList.size());
    }

    private void mockCursorCalls(Cursor mockCursor) {
        when(mockCursor.getCount()).thenReturn(1);
        when(mockCursor.moveToNext()).thenReturn(true, false);

        when(mockCursor.getColumnIndex(StoryColumns._ID)).thenReturn(0);
        when(mockCursor.getLong(0)).thenReturn(1L);

        when(mockCursor.getColumnIndex(StoryColumns.POSTER_NAME)).thenReturn(1);
        when(mockCursor.getString(1)).thenReturn("Story poster name");

        when(mockCursor.getColumnIndex(StoryColumns.SCORE)).thenReturn(2);
        when(mockCursor.getLong(2)).thenReturn(100L);

        when(mockCursor.getColumnIndex(StoryColumns.TITLE)).thenReturn(3);
        when(mockCursor.getString(3)).thenReturn("Story Title");

        when(mockCursor.getColumnIndex(StoryColumns.UNIX_TIME)).thenReturn(4);
        when(mockCursor.getLong(4)).thenReturn(new Date().getTime());

        when(mockCursor.getColumnIndex(StoryColumns.URL)).thenReturn(5);
        when(mockCursor.getString(5)).thenReturn("https://google.com/");
    }

    @Test
    public void testSaveStories() throws Exception {

    }
}