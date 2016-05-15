package dev.bltucker.nanodegreecapstone.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterListActivity;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailActivity;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragment;
import dev.bltucker.nanodegreecapstone.topstories.StoryListFragment;

public class MainActivity extends AppCompatActivity implements HomeView, StoryListFragment.Delegate {

    @Inject
    HomeViewPresenter homeViewPresenter;
    private boolean twoPaneMode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        MenuItem item = menu.findItem(R.id.menu_item_show_read_later);
        DrawableCompat.setTint(item.getIcon(), Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_show_read_later){
            homeViewPresenter.onShowReadLaterMenuClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);

        twoPaneMode = findViewById(R.id.story_detail_fragment_container) != null;

        if (null == savedInstanceState) {
            if(twoPaneMode){
                replaceDetailFragment(StoryDetailFragment.NO_STORY_SELECTED_POSITION);
            }
            homeViewPresenter.onViewCreated(this);
        } else {
            homeViewPresenter.onViewRestored(this);
        }
    }

    private void replaceDetailFragment(int storyPosition) {
        Bundle bundle = new Bundle();
        bundle.putInt(StoryDetailFragment.STORY_POSITION_BUNDLE_KEY, storyPosition);
        StoryDetailFragment storyDetailFragment = StoryDetailFragment.newInstance(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.story_detail_fragment_container, storyDetailFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        homeViewPresenter.onViewDestroyed(this);
        super.onDestroy();
    }

    @Override
    public void showReadLaterListView() {
        ReadLaterListActivity.launch(this);
    }

    @Override
    public void showCommentsView(int storyPosition) {
        if(twoPaneMode){
            replaceDetailFragment(storyPosition);
        } else {
            StoryDetailActivity.launch(this, storyPosition);
        }
    }
}
