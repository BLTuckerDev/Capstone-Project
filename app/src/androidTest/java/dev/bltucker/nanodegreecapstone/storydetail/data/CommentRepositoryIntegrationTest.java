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
import java.util.concurrent.TimeUnit;

import dev.bltucker.nanodegreecapstone.common.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.common.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.common.data.migrations.Version1to2;
import dev.bltucker.nanodegreecapstone.common.data.migrations.Version2to3;
import dev.bltucker.nanodegreecapstone.common.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.CommentRepository;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class CommentRepositoryIntegrationTest {

    CommentRepository objectUnderTest;

    private HackerNewsDatabase hackerNewsDatabase;

    private CommentsDao commentsDao;

    @Before
    public void setup() {
        hackerNewsDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), HackerNewsDatabase.class)
                .allowMainThreadQueries()
                .addMigrations(new Version1to2(1, 2))
                .addMigrations(new Version2to3(2, 3))
                .build();
        commentsDao = hackerNewsDatabase.commentsDao();
        objectUnderTest = new CommentRepository(commentsDao);
    }

    @After
    public void after() {
        hackerNewsDatabase.close();
    }


    @Test
    public void testGetStoryCommentsShouldReturnListofComments() throws InterruptedException {
        final int storyId = 1;

        List<Comment> fakeComments = new ArrayList<>();

        fakeComments.add(new Comment(1, storyId, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 1, 0));
        fakeComments.add(new Comment(2, storyId, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 1, 1));
        fakeComments.add(new Comment(3, storyId, "Ann Author", "Some More Comment Text", System.currentTimeMillis(), 2, 2));

        commentsDao.saveAll(fakeComments);

        TestObserver<Comment[]> testSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(storyId).subscribe(testSubscriber);

        testSubscriber.await(3, TimeUnit.SECONDS);

        testSubscriber.assertValue(comments -> comments.length == 3 && Arrays.asList(comments).containsAll(fakeComments));

        testSubscriber.dispose();
    }

    @Test
    public void testGetStoryCommentsWithInvalidStoryShouldReturnEmptyCommentList() throws InterruptedException {

        TestObserver<Comment[]> testSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(-1).subscribe(testSubscriber);

        testSubscriber.await(3, TimeUnit.SECONDS);

        testSubscriber.assertValue(comments -> comments.length == 0);

        testSubscriber.dispose();
    }

    @Test
    public void testSaveComment() throws InterruptedException {
        final int storyId = 1;

        Comment testComment = new Comment(1, storyId, "Some Author", "Comment Text", System.currentTimeMillis(), 1, 0);

        objectUnderTest.saveComment(testComment);

        TestObserver<Comment[]> afterSubscriber = new TestObserver<>();

        objectUnderTest.getCommentsForStoryId(1)
                .subscribe(afterSubscriber);

        afterSubscriber.await(3, TimeUnit.SECONDS);

        afterSubscriber.assertValue(comments -> {
            return comments[0].commentId == testComment.commentId;
        });

        afterSubscriber.dispose();
    }
}
