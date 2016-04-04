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
        objectUnderTest = new ReadingSession();
    }

    @Test
    public void testRead() throws Exception {
        final long[] commentRefs = new long[]{1l,2l,3l,4l,5l};
        final Date now = new Date();

        List<Comment> commentList = new ArrayList<>(commentRefs.length);
        for(int i = 0; i < commentRefs.length; i++){
            commentList.add(new Comment(commentRefs[i], "Comment Author", "Comment", now.getTime(), new long[0]));
        }

        Story testStory = new Story(1, "Brett", 1, now.getTime(), "Title", "https://google.com/", commentRefs);

        objectUnderTest.read(testStory, commentList);

        assertEquals(testStory, objectUnderTest.getCurrentStory());
        assertEquals(commentList, objectUnderTest.getCurrentStoryComments());

    }

    @Test
    public void testAddStories() throws Exception {
        final Date now = new Date();
        Story testStory = new Story(1, "Brett", 1, now.getTime(), "Title", "https://google.com/", new long[0]);
        List<Story> storyList = new ArrayList<>();
        storyList.add(testStory);

        objectUnderTest.setStories(storyList);

        assertEquals(1, objectUnderTest.getStories().size());
        assertEquals(storyList, objectUnderTest.getStories());
        assertEquals(0, objectUnderTest.getLargestStoryListIndexViewed());

    }
}