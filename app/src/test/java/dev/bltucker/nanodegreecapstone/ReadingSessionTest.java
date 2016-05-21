package dev.bltucker.nanodegreecapstone;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReadingSessionTest {

    public ReadingSession objectUnderTest;

    @Before
    public void setup(){
        objectUnderTest = new ReadingSession(150);
    }

    @Test
    public void testRead() throws Exception {
        //TODO remove this when we remove the current story global
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
    public void testSettingSameStoriesDoesNotDirtySession(){

        Story a = new Story(1, "A", 1, 0, "A Story", "", new Long[0]);
        Story b = new Story(2, "B", 1, 0, "B Story", "", new Long[0]);
        Story c = new Story(3, "C", 1, 0, "C Story", "", new Long[0]);

        List<Story> listA = new ArrayList<>();
        List<Story> listB = new ArrayList<>();


        listA.add(a);
        listA.add(b);
        listA.add(c);

        listB.add(a);
        listB.add(b);
        listB.add(c);


        objectUnderTest.setLatestSyncStories(listA);
        objectUnderTest.updateUserStoriesToLatestSync();

        objectUnderTest.setLatestSyncStories(listB);


        assertFalse(objectUnderTest.isStoryListIsDirty());



    }

}