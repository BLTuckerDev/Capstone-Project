package dev.bltucker.nanodegreecapstone.data;

import android.content.ContentResolver;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dev.bltucker.nanodegreecapstone.data.daos.CommentRefsDao;
import dev.bltucker.nanodegreecapstone.data.daos.StoryDao;
import dev.bltucker.nanodegreecapstone.models.Story;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContentProviderBackedStoryRepositoryTest {

    ContentResolver mockContentResolver;

    CommentRepository mockCommentRepository;

    ContentProviderBackedStoryRepository objectUnderTest;
    private StoryDao mockStoryDao;
    private CommentRefsDao mockCommentRefsDao;

    @Before
    public void setup() {
        mockContentResolver = mock(ContentResolver.class);
        mockCommentRepository = mock(CommentRepository.class);
        mockCommentRefsDao = mock(CommentRefsDao.class);
        mockStoryDao = mock(StoryDao.class);
        objectUnderTest = new ContentProviderBackedStoryRepository(mockStoryDao, mockCommentRefsDao);
    }

    @Test
    public void testGetAllStories() throws Exception {
        final List<Story> fakeStories = new ArrayList<>();

        when(mockStoryDao.getAllStories())
                .thenReturn(Flowable.just(fakeStories));

        TestObserver<List<Story>> testSubscriber = new TestObserver<>();

        objectUnderTest.getAllStories().subscribe(testSubscriber);
        testSubscriber.assertValue(fakeStories);
    }

    @Test
    public void testSaveStories() throws Exception {
        final Story[] fakeStories = new Story[]{
                new Story(1, "A. Poster", 100, System.currentTimeMillis(), "SOme Post", "https://blog.abnormallydriven.com/"),
                new Story(2, "A. Poster", 100, System.currentTimeMillis(), "SOme Post", "https://blog.abnormallydriven.com/"),
                new Story(3, "A?.Poster", 100, System.currentTimeMillis(), "SOme Post", "https://blog.abnormallydriven.com/")
        };

        doNothing().when(mockStoryDao).deleteAllStories();
        doNothing().when(mockCommentRefsDao).deleteAllCommentRefs();
        doNothing().when(mockStoryDao).saveStories(fakeStories);
        doNothing().when(mockCommentRefsDao).saveAllRefs(any());

        objectUnderTest.saveStories(fakeStories);

        verify(mockStoryDao, times(1)).deleteAllStories();
        verify(mockCommentRefsDao, times(1)).deleteAllCommentRefs();
        verify(mockStoryDao, times(1)).saveStories(fakeStories);
        verify(mockCommentRefsDao, times(1)).saveAllRefs(any());
    }
}