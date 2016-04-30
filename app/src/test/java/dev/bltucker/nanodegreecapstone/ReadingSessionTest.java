package dev.bltucker.nanodegreecapstone;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

import static org.junit.Assert.*;

public class ReadingSessionTest {

    public ReadingSession objectUnderTest;

    @Before
    public void setup(){
        objectUnderTest = new ReadingSession(150);
    }

    @Test
    public void testRead() throws Exception {
        final Date now = new Date();

        Comment firstComment = new Comment(1L, "First Comment Author", "Comment Text", now.getTime(), new long[]{2L});
        Comment childComment = new Comment(2L, "Second comment author", "Child Comment", now.getTime(), new long[]{});
        List<Comment> commentList = new ArrayList<>();
        commentList.add(firstComment);
        commentList.add(childComment);

        final Long[] commentRefs = new Long[]{firstComment.getId(), childComment.getId()};
        Story testStory = new Story(1, "Brett", 1, now.getTime(), "Title", "https://google.com/", commentRefs);

        objectUnderTest.read(testStory, commentList);

        assertEquals(testStory, objectUnderTest.getCurrentStory());
        assertEquals(commentList.size(), objectUnderTest.currentStoryCommentCount());
        assertNotNull(objectUnderTest.getCurrentStoryComment(0));
        assertEquals(firstComment, objectUnderTest.getCurrentStoryComment(0));
        assertEquals(firstComment, objectUnderTest.getParentComment(childComment.getId()));
        assertNull(objectUnderTest.getStory(Integer.MAX_VALUE));
        assertNull(objectUnderTest.getStory(Integer.MIN_VALUE));
        assertNull(objectUnderTest.getCurrentStoryComment(Integer.MAX_VALUE));
        assertNull(objectUnderTest.getCurrentStoryComment(Integer.MIN_VALUE));
    }

    @Test
    public void testAddStories() throws Exception {
        final Date now = new Date();
        Story testStory = new Story(1, "Brett", 1, now.getTime(), "Title", "https://google.com/", new Long[0]);
        List<Story> storyList = new ArrayList<>();
        storyList.add(testStory);

        objectUnderTest.setStories(storyList);

        assertEquals(1, objectUnderTest.storyCount());
        assertTrue(objectUnderTest.hasStories());

    }

    @Test
    public void testClearReadingSession(){
        final Date now = new Date();
        Story testStory = new Story(1, "Brett", 1, now.getTime(), "Title", "https://google.com/", new Long[0]);
        List<Story> storyList = new ArrayList<>();
        storyList.add(testStory);

        objectUnderTest.setStories(storyList);
        objectUnderTest.clearCurrentStory();

        assertNull(objectUnderTest.getCurrentStory());
        assertEquals(0, objectUnderTest.currentStoryCommentCount());
        assertNull(objectUnderTest.getCurrentStoryComment(0));


    }
}