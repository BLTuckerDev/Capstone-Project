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

import dev.bltucker.nanodegreecapstone.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.CommentRepository;
import io.reactivex.observers.TestObserver;


@RunWith(AndroidJUnit4.class)
public class CommentRepositoryIntegrationTest {

    CommentRepository objectUnderTest;

    private HackerNewsDatabase hackerNewsDatabase;

    private CommentsDao commentsDao;

    @Before
    public void setup() {
        hackerNewsDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), HackerNewsDatabase.class).build();
        commentsDao = hackerNewsDatabase.commentsDao();
        objectUnderTest = new CommentRepository(commentsDao, new FakeHackerNewsApiService());
    }

    @After
    public void after() {
        hackerNewsDatabase.close();
    }


    @Test
    public void testGetStoryCommentsShouldReturnListofComments() {

        List<Comment> fakeComments = new ArrayList<>();

        fakeComments.add(new Comment(1, 1, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 1, 0));
        fakeComments.add(new Comment(2, 1, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 1, 1));
        fakeComments.add(new Comment(3, 1, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 2, 2));

        commentsDao.saveAll(fakeComments);

        TestObserver<Comment[]> testSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(1).subscribe(testSubscriber);

        testSubscriber.assertValue(comments -> comments.length == 3 && Arrays.asList(comments).containsAll(fakeComments));

        testSubscriber.dispose();
    }

    @Test
    public void testGetStoryCommentsWithInvalidStoryShouldReturnEmptyCommentList() {

        TestObserver<Comment[]> testSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(-1).subscribe(testSubscriber);

        testSubscriber.assertValue(comments -> comments.length == 0);

        testSubscriber.dispose();
    }

    @Test
    public void testSaveComment() {
        Comment testComment = new Comment(1, 1, "Some Author", "Comment Text", System.currentTimeMillis(), 1, 0);

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
