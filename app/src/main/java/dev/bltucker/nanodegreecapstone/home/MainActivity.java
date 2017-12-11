package dev.bltucker.nanodegreecapstone.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterListActivity;
import dev.bltucker.nanodegreecapstone.settings.SettingsActivity;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailActivity;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragment;
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesFragment;

public class MainActivity extends AppCompatActivity implements HomeView, TopStoriesFragment.Delegate {

    @Inject
    HackerNewsDatabase hackerNewsDatabase;

    @Inject
    HomeViewPresenter homeViewPresenter;

    private boolean twoPaneMode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        MenuItem item = menu.findItem(R.id.menu_item_show_read_later);
        DrawableCompat.setTint(item.getIcon(), Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_show_read_later) {
            homeViewPresenter.onShowReadLaterMenuClick();
            return true;
        }

        if (item.getItemId() == R.id.menu_item_settings) {
            SettingsActivity.launch(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerInjector.getApplicationComponent().inject(this);

        twoPaneMode = findViewById(R.id.story_detail_fragment_container) != null;

        if (null == savedInstanceState) {
            if (twoPaneMode) {
                replaceDetailFragment(null);
            }
            homeViewPresenter.onViewCreated(this);
        } else {
            homeViewPresenter.onViewRestored(this);
        }
    }

    private void replaceDetailFragment(Story story) {
        StoryDetailFragment storyDetailFragment = StoryDetailFragment.Companion.newInstance(story);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.story_detail_fragment_container, storyDetailFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        homeViewPresenter.onViewDestroyed();
        super.onDestroy();
    }

    @Override
    public void showReadLaterListView() {
        ReadLaterListActivity.launch(this);
    }

    @Override
    public void showCommentsView(Story story) {
        if (twoPaneMode) {
            replaceDetailFragment(story);
        } else {
            StoryDetailActivity.Companion.launch(this, story);
        }
    }
}
