package dev.bltucker.nanodegreecapstone;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import io.reactivex.Observable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReadingSessionTest {

    public ReadingSession objectUnderTest;

    @Before
    public void setup(){
        EventBus mockEventBus = mock(EventBus.class);
        when(mockEventBus.subscribeTo(SyncCompletedEvent.class)).thenReturn(Observable.empty());

        objectUnderTest = new ReadingSession(150, mockEventBus);
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

    @Test
    public void testSettingDifferentStoriesShouldDirtySession(){
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
        listB.add(new Story(4, "D", 1,0, "D story", "", new Long[0]));

        objectUnderTest.setLatestSyncStories(listA);
        objectUnderTest.updateUserStoriesToLatestSync();

        objectUnderTest.setLatestSyncStories(listB);

        assertTrue(objectUnderTest.isStoryListIsDirty());

    }

}