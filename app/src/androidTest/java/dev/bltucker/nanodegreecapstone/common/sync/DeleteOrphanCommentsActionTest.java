package dev.bltucker.nanodegreecapstone.common.sync;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import dev.bltucker.nanodegreecapstone.common.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.common.data.daos.StoryDao;
import dev.bltucker.nanodegreecapstone.common.models.Comment;
import dev.bltucker.nanodegreecapstone.common.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.CommentRepository;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeleteOrphanCommentsActionTest {

    private DeleteOrphanCommentsAction objectUnderTest;
    private CommentRepository commentRepository;
    private HackerNewsDatabase hackerNewsDatabase;
    private StoryDao storyDao;

    @Before
    public void setUp() throws Exception {
        hackerNewsDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), HackerNewsDatabase.class).build();
        commentRepository = new CommentRepository(hackerNewsDatabase.commentsDao());
        storyDao = hackerNewsDatabase.storyDao();
        objectUnderTest = new DeleteOrphanCommentsAction(commentRepository);
    }

    @After
    public void tearDown() {
        hackerNewsDatabase.close();
    }

    @Test
    public void testWithOrphanCommentsShouldDeleteJustTheOrphans() throws Exception {
        //create orphan comments
        commentRepository.saveComment(new Comment(1, 2, "author", "comment Text", System.currentTimeMillis(), -1, 0));
        commentRepository.saveComment(new Comment(2, 2, "author", "comment Text", System.currentTimeMillis(), -1, 0));
        commentRepository.saveComment(new Comment(3, 2, "author", "comment Text", System.currentTimeMillis(), -1, 0));
        commentRepository.saveComment(new Comment(4, 2, "author", "comment Text", System.currentTimeMillis(), -1, 0));

        Comment[] beforeTaskArray = commentRepository.getRootOrphanComments();

        objectUnderTest.run();

        Comment[] afterTakeArray = commentRepository.getRootOrphanComments();

        assertTrue(beforeTaskArray.length > 0);
        assertTrue(afterTakeArray.length == 0);

    }

    @Test
    public void testWithNoOrphanCommentsShouldNotDeleteAnyComments() {
        int storyId = 2;
        Story[] stories = new Story[]{new Story(storyId, "some poster", 100, System.currentTimeMillis(), "A Title", "https://blog.abnormallydriven.com/")};

        storyDao.saveStories(stories);

        commentRepository.saveComment(new Comment(1, 2, "author", "comment Text", System.currentTimeMillis(), storyId, 0));
        commentRepository.saveComment(new Comment(2, 2, "author", "comment Text", System.currentTimeMillis(), storyId, 0));
        commentRepository.saveComment(new Comment(3, 2, "author", "comment Text", System.currentTimeMillis(), storyId, 0));
        commentRepository.saveComment(new Comment(4, 2, "author", "comment Text", System.currentTimeMillis(), storyId, 0));

        Comment[] beforeTaskArray = commentRepository.getRootOrphanComments();

        objectUnderTest.run();

        Comment[] afterTaskArray = commentRepository.getRootOrphanComments();

        assertTrue(beforeTaskArray.length == 0);
        assertTrue(afterTaskArray.length == 0);
        assertTrue(commentRepository.getChildComments(storyId).length == 4);

    }
}