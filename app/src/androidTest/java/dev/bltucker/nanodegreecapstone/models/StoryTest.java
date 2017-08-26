package dev.bltucker.nanodegreecapstone.models;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SmallTest
public class StoryTest {

    @Test
    public void testEquals() throws Exception {
        Story storyA = new Story(1, "a name", 1, 0, "A Title", "url", new Long[0]);
        Story storyB = new Story(1, "another name", 2, 1, "Another title", "another url", new Long[0]);

        assertTrue(storyA.equals(storyB));
    }

    @Test
    public void testWriteToParcel() throws Exception {
        Story storyA = new Story(1, "a name", 1, 0, "A Title", "url", new Long[]{1L});

        Parcel parcel = Parcel.obtain();

        storyA.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        assertEquals(storyA.getId(), parcel.readLong());
        assertEquals(storyA.getPosterName(), parcel.readString());
        assertEquals(storyA.getScore(), parcel.readLong());
        assertEquals(storyA.getUnixTime(), parcel.readLong());
        assertEquals(storyA.getTitle(), parcel.readString());
        assertEquals(storyA.getUrl(), parcel.readString());
        assertEquals(1, parcel.readInt());

        long[] primitiveArray = new long[1];
        parcel.readLongArray(primitiveArray);
        assertEquals(1L, primitiveArray[0]);
        assertEquals(1, primitiveArray.length);

    }
}