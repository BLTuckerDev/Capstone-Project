package dev.bltucker.nanodegreecapstone.home;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterListActivity;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailActivity;
import dev.bltucker.nanodegreecapstone.topstories.StoryListFragment;

public class MainActivity extends AppCompatActivity implements HomeView, StoryListFragment.Delegate {

    private static final String STORY_LIST_FRAGMENT_TAG = "storyListFragment";

    @Inject
    HomeViewPresenter homeViewPresenter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
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
        if (null == savedInstanceState) {
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
        StoryListFragment storyListFragment = StoryListFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.main_activity_coordinator_layout, storyListFragment, STORY_LIST_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void showReadLaterListView() {
        ReadLaterListActivity.launch(this);
    }

    @Override
    public void showCommentsView(int storyPosition) {
        //TODO in tablet mode we will just show a different story in the details fragment here.
        //.updateStory() since the model will already be changed
        //TODO use a shared element transition here
        StoryDetailActivity.launch(this, storyPosition);
    }
}
