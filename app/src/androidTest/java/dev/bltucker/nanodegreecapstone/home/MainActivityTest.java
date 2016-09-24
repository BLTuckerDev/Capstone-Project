package dev.bltucker.nanodegreecapstone.home;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.injection.ApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.injection.TestApplicationResourcesModule;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @BeforeClass
    public static void testShit(){
        Log.d("TEST", "Wtf");
    }

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            MainActivity.class,
            true,    // initialTouchMode
            false);  // launchActivity. False to set intent per method

    @Before
    public void setup(){
        Context context = getInstrumentation().getTargetContext().getApplicationContext();

        final ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationResourcesModule(new TestApplicationResourcesModule((CapstoneApplication) context))
                .build();

        DaggerInjector.initializeInjector(applicationComponent);

    }


    @Test
    public void testMainActivity(){

        activityRule.launchActivity(null);

        HomeViewPresenter homeViewPresenter = ((MainActivity) activityRule.getActivity()).homeViewPresenter;

        onView(withId(R.id.content_container)).check(matches(isDisplayed()));




    }

}
