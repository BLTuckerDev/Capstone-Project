package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.CommentRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class CommentRepositoryIntegrationTest {

    CommentRepository objectUnderTest;

    private HackerNewsDatabase hackerNewsDatabase;

    private CommentsDao commentsDao;
    private HackerNewsApiService mockHackerNewsService;

    @Before
    public void setup() {
        mockHackerNewsService = mock(HackerNewsApiService.class);
        hackerNewsDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), HackerNewsDatabase.class).build();
        commentsDao = hackerNewsDatabase.commentsDao();
        objectUnderTest = new CommentRepository(commentsDao, mockHackerNewsService);
    }

    @After
    public void after() {
        hackerNewsDatabase.close();
    }


    @Test
    public void testGetStoryCommentsShouldReturnListofComments() {
        final int storyId = 1;

        when(mockHackerNewsService.getStory(storyId)).thenReturn(Single.never());

        List<Comment> fakeComments = new ArrayList<>();

        fakeComments.add(new Comment(1, storyId, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 1, 0));
        fakeComments.add(new Comment(2, storyId, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 1, 1));
        fakeComments.add(new Comment(3, storyId, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 2, 2));

        commentsDao.saveAll(fakeComments);

        TestObserver<Comment[]> testSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(storyId).subscribe(testSubscriber);

        testSubscriber.assertValue(comments -> comments.length == 3 && Arrays.asList(comments).containsAll(fakeComments));

        testSubscriber.dispose();
    }

    @Test
    public void testGetStoryCommentsWithInvalidStoryShouldReturnEmptyCommentList() {

        when(mockHackerNewsService.getStory(anyLong())).thenReturn(Single.never());

        TestObserver<Comment[]> testSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(-1).subscribe(testSubscriber);

        testSubscriber.assertValue(comments -> comments.length == 0);

        testSubscriber.dispose();
    }

    @Test
    public void testSaveComment() {
        final int storyId = 1;
        
        when(mockHackerNewsService.getStory(anyLong())).thenReturn(Single.never());


        Comment testComment = new Comment(1, storyId, "Some Author", "Comment Text", System.currentTimeMillis(), 1, 0);

        TestObserver<Comment[]> beforeSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(1).subscribe(beforeSubscriber);

        beforeSubscriber.assertValue(comments -> comments.length == 0);

        beforeSubscriber.dispose();

        objectUnderTest.saveComment(testComment);

        TestObserver<Comment[]> afterSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(1).subscribe(afterSubscriber);

        afterSubscriber.assertValue(comments -> Arrays.asList(comments).contains(testComment));

        afterSubscriber.dispose();
    }
}
