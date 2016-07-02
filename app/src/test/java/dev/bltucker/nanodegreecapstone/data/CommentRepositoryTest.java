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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@SuppressLint("unchecked")
@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContentResolver.class, LruCache.class, Cursor.class, Uri.class })
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class CommentRepositoryTest {

    HackerNewsApiService mockHackerNewsApiService;

    ContentResolver mockContentResolver;

    LruCache<Long, List<Comment>> mockCache;

    CommentRepository objectUnderTest;
    private Comment fakeComment;
    private long fakeCommentId;

    @Before
    public void setUp() throws Exception {
        fakeCommentId = 1L;
        fakeComment = new Comment(fakeCommentId, "Author", "Comment Text", new Date().getTime(), new long[0]);

        mockHackerNewsApiService = mock(HackerNewsApiService.class);
        mockContentResolver = mock(ContentResolver.class);
        mockCache = mock(LruCache.class);
        objectUnderTest = new CommentRepository(mockHackerNewsApiService, mockContentResolver, mockCache);
    }

    @Test
    public void testGetStoryCommentsCacheHit() throws Exception {
        List<Comment> commentList = new ArrayList<>();
        commentList.add(fakeComment);

        when(mockCache.get(fakeCommentId)).thenReturn(commentList);

        mockCache.put(fakeCommentId, commentList);

        List<Comment> storyComments = objectUnderTest.getStoryComments(fakeCommentId).toBlocking().first();

        assertEquals(storyComments.size(), commentList.size());
        assertEquals(commentList, storyComments);
    }

    @Test
    public void testGetStoryCommentsCacheMiss(){
        int storyId = 1;
        mockContentProviderCalls(storyId);

        when(mockHackerNewsApiService.getComment(1)).thenReturn(Observable.just(fakeComment));

        List<Comment> commentList = objectUnderTest.getStoryComments(fakeCommentId).toBlocking().first();

        assertEquals(1, commentList.size());
        assertEquals(fakeComment, commentList.get(0));

    }

    private void mockContentProviderCalls(int storyId) {
        Uri mockUri = mock(Uri.class);
        mockStatic(Uri.class);
        when(Uri.parse(anyString())).thenReturn(mockUri);
        Cursor mockCursor = mock(Cursor.class);

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
    }

    @Test
    public void testClearInMemoryCache() throws Exception {
        objectUnderTest.clearInMemoryCache();

        verify(mockCache, times(1)).evictAll();
    }
}