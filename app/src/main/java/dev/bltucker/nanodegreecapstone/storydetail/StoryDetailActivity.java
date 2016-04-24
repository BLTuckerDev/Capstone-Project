package dev.bltucker.nanodegreecapstone.storydetail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dev.bltucker.nanodegreecapstone.R;

public class StoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        if(null == savedInstanceState){
            Fragment storyDetailFragment = StoryDetailFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.story_activity_coordinator_layout, storyDetailFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }
}
