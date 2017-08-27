package dev.bltucker.nanodegreecapstone.data;

import android.annotation.SuppressLint;
import android.content.ContentResolver;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.models.Comment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressLint("unchecked")
@SuppressWarnings("unchecked")
public class CommentRepositoryTest {

    CommentRepository objectUnderTest;
    private CommentsDao mockCommentsDao;


    @Before
    public void setUp() throws Exception {
        mockCommentsDao = mock(CommentsDao.class);
        objectUnderTest = new CommentRepository(mockCommentsDao);
    }

    @Test
    public void testGetStoryComments_WithValidStoryId_ShouldReturnCommentList(){
        int storyId = 1;

        Comment[] fakeComments = new Comment[]{
                new Comment(1, storyId, "Some Author", "Witty comment", System.currentTimeMillis(), storyId, 1),
                new Comment(2, storyId, "A Troll", "trolololo", System.currentTimeMillis(), storyId, 1),
                new Comment(3, storyId, "Random Poster", "Randomness", System.currentTimeMillis(), storyId, 1),
        };

        when(mockCommentsDao.getStoryComments(storyId)).thenReturn(fakeComments);

        List<Comment> storyComments = objectUnderTest.getStoryComments(storyId).blockingFirst();

        assertEquals(3, storyComments.size());
        assertTrue(storyComments.contains(fakeComments[0]));
        assertTrue(storyComments.contains(fakeComments[1]));
        assertTrue(storyComments.contains(fakeComments[2]));
    }

    @Test
    public void testGetStoryCommentsWithInvalidStoryIdShouldReturnEmptyList(){
        int storyId = -1;

        when(mockCommentsDao.getStoryComments(storyId)).thenReturn(new Comment[0]);

        List<Comment> storyComments = objectUnderTest.getStoryComments(storyId).blockingFirst();

        assertTrue(storyComments.isEmpty());
    }

}