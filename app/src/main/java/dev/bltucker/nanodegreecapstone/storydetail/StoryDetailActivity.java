package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dev.bltucker.nanodegreecapstone.R;
import timber.log.Timber;

public class StoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        if(null == savedInstanceState){
            addStoryDetailFragment(getIntent().getExtras());
        }
    }

    private void addStoryDetailFragment(Bundle bundle){
        if(null == bundle){
            Timber.e("StoryDetailActivity launched without a bundle!");
            return;
        }

        if(!bundle.containsKey(STORY_POSITION_BUNDLE_KEY)){
            Timber.e("StoryDetailActivity bundle is missing the story position");
            return;
        }

        Fragment storyDetailFragment = StoryDetailFragment.newInstance(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.story_activity_coordinator_layout, storyDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public static void launch(Context context, int storyPosition) {
        Intent launchIntent = new Intent(context, StoryDetailActivity.class);
        launchIntent.putExtra(STORY_POSITION_BUNDLE_KEY, storyPosition);
        context.startActivity(launchIntent);
    }

    public static final String STORY_POSITION_BUNDLE_KEY = "storyPosition";
}
