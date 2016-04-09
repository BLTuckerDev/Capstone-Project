package dev.bltucker.nanodegreecapstone.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.topstories.StoryListFragment;

public class MainActivity extends AppCompatActivity implements HomeView {

    public static final String STORY_LIST_FRAGMENT_TAG = "storyListFragment";

    @Inject
    HomeViewPresenter homeViewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
        if(null == savedInstanceState){
            homeViewPresenter.onViewCreated(this);
        } else {
            homeViewPresenter.onViewRestored(this);
        }
    }

    @Override
    protected void onDestroy() {
        homeViewPresenter.onViewDestroyed(this);
        super.onDestroy();
    }

    @Override
    public void showStoryList() {
        getSupportFragmentManager()
              .beginTransaction()
              .add(R.id.main_activity_coordinator_layout, StoryListFragment.newInstance(), STORY_LIST_FRAGMENT_TAG)
              .commit();
    }
}
