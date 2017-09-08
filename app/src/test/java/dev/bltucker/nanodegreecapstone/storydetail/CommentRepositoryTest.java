package dev.bltucker.nanodegreecapstone.storydetail;

import android.annotation.SuppressLint;
import android.content.ContentResolver;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.bltucker.nanodegreecapstone.data.FakeHackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressLint("unchecked")
@SuppressWarnings("unchecked")
public class CommentRepositoryTest {

    CommentRepository objectUnderTest;
    private CommentsDao mockCommentsDao;
    private FakeHackerNewsApiService hackerNewsApiService;


    @Before
    public void setUp() throws Exception {
        mockCommentsDao = mock(CommentsDao.class);
        hackerNewsApiService = new FakeHackerNewsApiService();
        objectUnderTest = new CommentRepository(mockCommentsDao, hackerNewsApiService);
    }

    @Test
    public void testGetStoryComments_WithValidStoryId_ShouldReturnCommentList() {
        int storyId = 1;
        Story fakeStory = new Story(storyId, "Story Author", 100, System.currentTimeMillis(), "Some Title", "http://bltucker.com/");

        Comment[] fakeComments = new Comment[]{
                new Comment(1, storyId, "Some Author", "Witty comment", System.currentTimeMillis(), storyId, 1),
                new Comment(2, storyId, "A Troll", "trolololo", System.currentTimeMillis(), storyId, 1),
                new Comment(3, storyId, "Random Poster", "Randomness", System.currentTimeMillis(), storyId, 1),
        };

        List<Long> storyIdList = new ArrayList<>();

        storyIdList.add((long) storyId);

        Map<Long, Story> storyMap = new HashMap<>();

        storyMap.put((long) storyId, fakeStory);

        Map<Long, Comment> commentsMap = new HashMap<>();

        commentsMap.put(fakeComments[0].id, fakeComments[0]);
        commentsMap.put(fakeComments[1].id, fakeComments[1]);
        commentsMap.put(fakeComments[2].id, fakeComments[2]);

        hackerNewsApiService.addFakeData(storyIdList, storyMap, commentsMap);

        when(mockCommentsDao.getStoryComments(storyId)).thenReturn(fakeComments);
        List<Comment> storyComments = Arrays.asList(objectUnderTest.getCommentsForStoryId(storyId).blockingFirst());

        assertEquals(3, storyComments.size());
        assertTrue(storyComments.contains(fakeComments[0]));
        assertTrue(storyComments.contains(fakeComments[1]));
        assertTrue(storyComments.contains(fakeComments[2]));
    }

    @Test
    public void testGetStoryCommentsWithInvalidStoryIdShouldReturnEmptyList() {
        int storyId = -1;

        when(mockCommentsDao.getStoryComments(storyId)).thenReturn(new Comment[0]);

        List<Comment> storyComments = Arrays.asList(objectUnderTest.getCommentsForStoryId(storyId).blockingFirst());

        assertTrue(storyComments.isEmpty());
    }

}