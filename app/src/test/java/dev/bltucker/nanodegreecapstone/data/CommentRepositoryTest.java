package dev.bltucker.nanodegreecapstone.data;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.LruCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;
import rx.Subscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@SuppressLint("unchecked")
@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContentResolver.class, LruCache.class, Cursor.class, Uri.class })
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class CommentRepositoryTest {

    ContentResolver mockContentResolver;

    CommentRepository objectUnderTest;


    @Before
    public void setUp() throws Exception {
        mockContentResolver = mock(ContentResolver.class);
        objectUnderTest = new CommentRepository(mockContentResolver);
    }

    @Test
    public void testGetStoryComments_WithValidStoryId_ShouldReturnCommentList(){
        int storyId = 1;

        mockContentProviderCalls(storyId);

        List<Comment> storyComments = objectUnderTest.getStoryComments(storyId).toBlocking().first();

        assertEquals(2, storyComments.size());
    }

    @Test
    public void testGetStoryComments_WithInvalidStoryId_ShouldReturnObservableError(){
        Uri mockUri = mock(Uri.class);
        mockStatic(Uri.class);
        when(Uri.parse(anyString())).thenReturn(mockUri);
        int storyId = 1;

        when(mockContentResolver.query(SchematicContentProviderGenerator.CommentPaths.withStoryId(String.valueOf(storyId)),
                null,
                null,
                null,
                null))
                .thenReturn(null);

        objectUnderTest.getStoryComments(storyId).subscribe(new Subscriber<List<Comment>>() {
            @Override
            public void onCompleted() {   }

            @Override
            public void onError(Throwable e) {
                assertNotNull(e);
            }

            @Override
            public void onNext(List<Comment> comments) {
                fail();
            }
        });

    }

    @Test
    public void testGetCommentIds_WithValidStoryId_ShouldReturnCommentIdsArray(){
        Uri mockUri = mock(Uri.class);
        mockStatic(Uri.class);
        when(Uri.parse(anyString())).thenReturn(mockUri);
        Cursor mockCursor = mock(Cursor.class);
        int storyId = 1;

        when(mockContentResolver.query(SchematicContentProviderGenerator.CommentRefs.withStoryId(String.valueOf(storyId)),
                null,
                null,
                null,
                null))
                .thenReturn(mockCursor);

        when(mockCursor.getCount()).thenReturn(1);
        when(mockCursor.moveToNext()).thenReturn(true, false);
        when(mockCursor.getColumnIndex(CommentRefsColumns._ID)).thenReturn(1);
        when(mockCursor.getLong(1)).thenReturn(1L);
        doNothing().when(mockCursor).close();

        Long[] commentIds = objectUnderTest.getCommentIds(storyId);

        assertEquals(1, commentIds.length);
    }

    @Test
    public void testGetCommentIds_WithInvalidStoryId_ShouldReturnEmptyCommentsIdArray(){
        Uri mockUri = mock(Uri.class);
        mockStatic(Uri.class);
        when(Uri.parse(anyString())).thenReturn(mockUri);
        int storyId = 1;

        when(mockContentResolver.query(SchematicContentProviderGenerator.CommentRefs.withStoryId(String.valueOf(storyId)),
                null,
                null,
                null,
                null))
                .thenReturn(null);

        Long[] commentIds = objectUnderTest.getCommentIds(1);

        assertEquals(0, commentIds.length);
    }


    private void mockContentProviderCalls(int storyId) {
        Uri mockUri = mock(Uri.class);
        mockStatic(Uri.class);
        when(Uri.parse(anyString())).thenReturn(mockUri);
        Cursor mockCursor = mock(Cursor.class);

        when(mockContentResolver.query(SchematicContentProviderGenerator.CommentPaths.withStoryId(String.valueOf(storyId)),
                null,
                null,
                null,
                null))
                .thenReturn(mockCursor);

        when(mockCursor.getCount()).thenReturn(2);
        when(mockCursor.moveToNext()).thenReturn(true, true, false);

        when(mockCursor.getLong(mockCursor.getColumnIndex(CommentColumns._ID))).thenReturn(1L, 2L);
        when(mockCursor.getString(mockCursor.getColumnIndex(CommentColumns.AUTHOR_NAME))).thenReturn("Author one", "Author two");
        when(mockCursor.getString(mockCursor.getColumnIndex(CommentColumns.COMMENT_TEXT))).thenReturn("Comment One Text", "Comment Two Text");
        when(mockCursor.getLong(mockCursor.getColumnIndex(CommentColumns.UNIX_POST_TIME))).thenReturn(System.currentTimeMillis());
        when(mockCursor.getLong(mockCursor.getColumnIndex(CommentColumns.PARENT_ID))).thenReturn(10L, 20L);
        doNothing().when(mockCursor).close();
    }
}