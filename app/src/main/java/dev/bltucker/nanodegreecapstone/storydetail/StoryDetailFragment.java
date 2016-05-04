package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.settings.SettingsActivity;

public class StoryDetailFragment extends Fragment implements StoryDetailView {

    @Bind(R.id.story_title_textview)
    TextView storyTitleTextView;

    @Bind(R.id.story_url_textview)
    TextView storyUrlTextView;

    @Bind(R.id.poster_name_textview)
    TextView storyPosterTextView;

    @Bind(R.id.score_textview)
    TextView storyScoreTextView;

    @Bind(R.id.comment_list_recyclerview)
    RecyclerView recyclerView;

    @Bind(R.id.read_button)
    Button readButton;

    @Bind(R.id.content_container)
    LinearLayout contentContainer;

    @Bind(R.id.loading_container)
    LinearLayout loadingContainer;

    @Inject
    StoryCommentsAdapter commentsAdapter;

    @Inject
    StoryDetailViewPresenter presenter;

    @Inject
    ReadingSession readingSession;

    ShareActionProvider shareActionProvider;
    MenuItem shareMenuItem;

    public StoryDetailFragment() {
        // Required empty public constructor
    }

    public static StoryDetailFragment newInstance(Bundle argBundle) {
        StoryDetailFragment storyDetailFragment = new StoryDetailFragment();
        storyDetailFragment.setArguments(new Bundle(argBundle));
        return storyDetailFragment;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        shareMenuItem = menu.findItem(R.id.menu_item_share_story);
        shareMenuItem.setVisible(false);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(readingSession.getCurrentStory() != null && shareActionProvider != null){
            setupShareActionProvider();
            shareMenuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_item_settings){
            SettingsActivity.launch(this.getActivity());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupShareActionProvider(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, readingSession.getCurrentStory().getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, readingSession.getCurrentStory().getUrl());
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_story_detail, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(commentsAdapter);
        if(null == savedInstanceState){
            int storyPosition = getArguments().getInt(StoryDetailActivity.STORY_POSITION_BUNDLE_KEY);
            presenter.onViewCreated(this, getLoaderManager(), storyPosition);
        } else {
            presenter.onViewRestored(this, getLoaderManager());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResumed(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onViewPaused();
    }

    @Override
    public void onDestroy() {
        presenter.onViewDestroyed();
        super.onDestroy();
    }

    @Override
    public void showStory() {
        if(getActivity() != null){
            getActivity().invalidateOptionsMenu();
        }
        Story story = readingSession.getCurrentStory();
        storyTitleTextView.setText(story.getTitle());
        storyUrlTextView.setText(story.getUrl());
        storyPosterTextView.setText(String.format(getString(R.string.by_poster), story.getPosterName()));
        storyScoreTextView.setText(String.format(getString(R.string.story_score), story.getScore()));
        if(null == story.getUrl()){
            readButton.setVisibility(View.INVISIBLE);
        } else {
            readButton.setVisibility(View.VISIBLE);
        }

        loadingContainer.setVisibility(View.INVISIBLE);
        contentContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showComments() {
        commentsAdapter.reset();
    }

    @OnClick(R.id.read_button)
    public void onReadButtonClick(View v){
        presenter.onReadButtonClicked();
    }

    @Override
    public void showStoryPostUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
