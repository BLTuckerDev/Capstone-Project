package dev.bltucker.nanodegreecapstone.data;


import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.injection.ApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.injection.TestApplicationResourcesModule;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import dev.bltucker.nanodegreecapstone.models.Story;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StoryProviderTests extends ProviderTestCase2<StoryProvider> {

    public StoryProviderTests() {
        super(StoryProvider.class, StoryProvider.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());

        final ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationResourcesModule(new TestApplicationResourcesModule((CapstoneApplication) getInstrumentation().getTargetContext().getApplicationContext()))
                .build();

        DaggerInjector.initializeInjector(applicationComponent);
        super.setUp();
    }


    @Test
    public void testStoryProvider() {
        StoryProvider provider = getProvider();

        provider.inject();

        final Story[] fakeStories = new Story[]{
                new Story(1, "A. Poster", 100, System.currentTimeMillis(), "Some Post", "https://blog.abnormallydriven.com/"),
                new Story(2, "A. Poster", 100, System.currentTimeMillis(), "Some Post", "https://blog.abnormallydriven.com/"),
                new Story(3, "A?.Poster", 100, System.currentTimeMillis(), "Some Post", "https://blog.abnormallydriven.com/")
        };

        provider.storyDao.saveStories(fakeStories);

        ReadLaterStory fakeReadLaterStory = new ReadLaterStory(1, "Some Poster", "Read Me Later", "http://blog.abnormallydriven.com/");

        provider.readLaterStoryDao.saveStory(fakeReadLaterStory);


        Cursor storyCursor = getMockContentResolver().query(StoryProvider.STORIES_URI, null, null, null, null);

        assertTrue(storyCursor.getCount() == 3);

        Cursor readLaterCursor = getMockContentResolver().query(StoryProvider.READ_LATER_URI, null, null, null, null);

        assertTrue(readLaterCursor.getCount() == 1);

    }
}

