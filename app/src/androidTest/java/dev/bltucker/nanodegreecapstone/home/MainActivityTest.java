package dev.bltucker.nanodegreecapstone.home;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dev.bltucker.nanodegreecapstone.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            MainActivity.class,
            true,    // initialTouchMode
            true);  // launchActivity. False to set intent per method

    @Test
    public void testMainActivity(){

        HomeViewPresenter homeViewPresenter = ((MainActivity) activityRule.getActivity()).homeViewPresenter;

        onView(withId(R.id.content_container)).check(matches(isDisplayed()));




    }

}
