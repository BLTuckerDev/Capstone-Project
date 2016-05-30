package dev.bltucker.nanodegreecapstone.storydetail;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.settings.SettingsActivity;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_story_detail_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_item_settings){
            SettingsActivity.launch(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addStoryDetailFragment(Bundle bundle){
        if(null == bundle){
            Timber.e("StoryDetailActivity launched without a bundle!");
            return;
        }

        if(!bundle.containsKey(StoryDetailFragment.STORY_BUNDLE_KEY)){
            Timber.e("StoryDetailActivity bundle is missing the story position");
            return;
        }

        Story story = bundle.getParcelable(StoryDetailFragment.STORY_BUNDLE_KEY);
        Fragment storyDetailFragment = StoryDetailFragment.newInstance(story);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.story_activity_coordinator_layout, storyDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public static void launch(Activity activity, @Nullable Story story) {
        Intent launchIntent = new Intent(activity, StoryDetailActivity.class);
        launchIntent.putExtra(StoryDetailFragment.STORY_BUNDLE_KEY, story);
        ActivityCompat.startActivity(activity, launchIntent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }
}
