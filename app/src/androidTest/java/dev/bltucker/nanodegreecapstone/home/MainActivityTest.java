package dev.bltucker.nanodegreecapstone.home;

import android.content.Context;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationComponent;
import dev.bltucker.nanodegreecapstone.common.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.common.injection.TestApplicationResourcesModule;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

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

        EventBus eventBus = DaggerInjector.getApplicationComponent().eventBus();

        Timber.d("Registered idling resource");
        eventBus.subscribeTo(SyncCompletedEvent.class)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onComplete() {       }

                    @Override
                    public void onError(Throwable e) {   }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Timber.d("Resource is now idle");
                    }
                });

    }

    @After
    public void teardown(){
    }

    @Test
    public void testMainActivity(){
        activityRule.launchActivity(null);
        Timber.d("Main activity has launched");

        onView(withText(activityRule.getActivity().getString(R.string.app_name))).check(matches(isDisplayed()));

        onView(withId(R.id.story_list_recyclerview)).check(matches(isDisplayed()));

    }


    public class SyncAdapterIdlingResource implements IdlingResource {

        private ResourceCallback resourceCallback;
        private volatile boolean isIdle;

        public SyncAdapterIdlingResource(){
            isIdle = false;
        }


        @Override
        public String getName() {
            return this.getClass().getName();
        }

        @Override
        public boolean isIdleNow() {
            if(isIdle && resourceCallback != null){
                resourceCallback.onTransitionToIdle();
            }
            return isIdle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
        }


        public synchronized void setIdle(boolean idle) {
            isIdle = idle;
        }
    }

}
