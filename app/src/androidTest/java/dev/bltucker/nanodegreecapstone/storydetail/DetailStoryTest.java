package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

import static org.junit.Assert.*;


public class DetailStoryTest {

    Story fakeStory;
    List<Comment> fakeComments;

    DetailStory objectUnderTest;


    @Before
    public void setup(){
        Long[] commentIds = new Long[]{ 1L, 2L, 4L, 5L};
        fakeStory = new Story(1L,
                "John Doe",
                100,
                new Date().getTime(),
                "A Story Title",
                "https://google.com/",
                commentIds);

        fakeComments = new ArrayList<>();
        for (int i = 0; i < commentIds.length; i++) {
            fakeComments.add(new Comment(commentIds[i],
                    "Comment Author " + i,
                    "Comment Text" + i,
                    new Date().getTime(),
                    new long[0]));

            objectUnderTest = new DetailStory(fakeStory);
        }
    }


    @Test
    public void testParcelConstructor(){
        Parcel input = Parcel.obtain();

        input.writeParcelable(fakeStory, 0);
        input.writeTypedList(fakeComments);
        input.setDataPosition(0);

        objectUnderTest = new DetailStory(input);

        assertEquals(fakeStory.getId(), objectUnderTest.getStoryId());
        assertEquals(fakeStory.getPosterName(), objectUnderTest.getPosterName());
        assertEquals(fakeStory.getScore(), objectUnderTest.getScore());
        assertEquals(fakeStory.getTitle(), objectUnderTest.getTitle());
        assertEquals(fakeStory.getUrl(), objectUnderTest.getUrl());
        assertArrayEquals(fakeStory.getCommentIds(), objectUnderTest.getCommentIds());
        assertEquals(fakeComments, objectUnderTest.getUnmodifiableCommentList());
    }

    @Test
    public void testWriteToParcel(){
        Parcel output = Parcel.obtain();

        objectUnderTest.setComments(fakeComments);
        objectUnderTest.writeToParcel(output, 0);
        output.setDataPosition(0);

        assertEquals(fakeStory, output.readParcelable(Story.class.getClassLoader()));
        List<Comment> comments = new ArrayList<>();
        output.readTypedList(comments, Comment.CREATOR);
        assertEquals(fakeComments, comments);
    }

}