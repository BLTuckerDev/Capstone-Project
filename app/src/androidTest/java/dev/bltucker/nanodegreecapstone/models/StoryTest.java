package dev.bltucker.nanodegreecapstone.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import static dev.bltucker.nanodegreecapstone.data.StoryColumns.POSTER_NAME;
import static dev.bltucker.nanodegreecapstone.data.StoryColumns.RANK;
import static dev.bltucker.nanodegreecapstone.data.StoryColumns.SCORE;
import static dev.bltucker.nanodegreecapstone.data.StoryColumns.TITLE;
import static dev.bltucker.nanodegreecapstone.data.StoryColumns.UNIX_TIME;
import static dev.bltucker.nanodegreecapstone.data.StoryColumns.URL;
import static dev.bltucker.nanodegreecapstone.data.StoryColumns._ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SmallTest
public class StoryTest {

    @Test
    public void testMapToContentValues() throws Exception {
        final Long[] commentIds = new Long[]{0L, 1L, 2L};
        Story mapMe = new Story(1, "Brett", 100, 1, "A Title", "https://google.com", commentIds);
        mapMe.setStoryRank(1);

        ContentValues contentValues = Story.mapToContentValues(mapMe);

        assertEquals(mapMe.getId(), contentValues.get(_ID));
        assertEquals(mapMe.getPosterName(), contentValues.get(POSTER_NAME));
        assertEquals(mapMe.getScore(), contentValues.get(SCORE));
        assertEquals(mapMe.getTitle(), contentValues.get(TITLE));
        assertEquals(mapMe.getUnixTime(), contentValues.get(UNIX_TIME));
        assertEquals(mapMe.getUrl(), contentValues.get(URL));
        assertEquals(mapMe.getStoryRank(), contentValues.get(RANK));

    }

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