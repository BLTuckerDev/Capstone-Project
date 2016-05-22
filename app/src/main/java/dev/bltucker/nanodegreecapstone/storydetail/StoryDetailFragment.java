package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
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

public class StoryDetailFragment extends Fragment implements StoryDetailView {

    public static final String STORY_POSITION_BUNDLE_KEY = "storyPosition";
    public static final int NO_STORY_SELECTED_POSITION = -1;

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

    @Bind(R.id.loading_container)
    LinearLayout loadingContainer;

    @Bind(R.id.empty_view_container)
    View emptyViewContainer;

    @Bind(R.id.detail_header_cardview)
    CardView headerView;

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

        MenuItem saveItem = menu.findItem(R.id.menu_item_save_story);
        DrawableCompat.setTint(saveItem.getIcon(), Color.WHITE);
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

        if(item.getItemId() == R.id.menu_item_save_story){
            presenter.onSaveStoryClick();
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
        CapstoneApplication.getApplication().getApplicationComponent()
                .storyDetailComponent(new StoryDetailFragmentModule(this))
                .inject(this);
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
            int storyPosition = getArguments() != null ? getArguments().getInt(STORY_POSITION_BUNDLE_KEY, -1) : -1;
            presenter.onViewCreated(this, storyPosition);
        } else {
            presenter.onViewRestored(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResumed(this);
    }

    @Override
    public void onPause() {
        presenter.onViewPaused();
        super.onPause();
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

        if(null == story){
            return;
        }

        storyTitleTextView.setText(story.getTitle());
        storyUrlTextView.setText(story.getUrl());
        storyPosterTextView.setText(String.format(getString(R.string.by_poster), story.getPosterName()));
        storyScoreTextView.setText(String.format(getString(R.string.story_score), story.getScore()));
        if(null == story.getUrl()){
            readButton.setVisibility(View.INVISIBLE);
        } else {
            readButton.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void showStorySaveConfirmation() {
        Toast.makeText(getContext(), getString(R.string.story_saved), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showCommentsLoadingSpinner() {
        recyclerView.setVisibility(View.INVISIBLE);
        emptyViewContainer.setVisibility(View.INVISIBLE);
        loadingContainer.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCommentsLoadingSpinner() {
        loadingContainer.setVisibility(View.INVISIBLE);
        emptyViewContainer.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyView() {
        loadingContainer.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        emptyViewContainer.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSelectAStoryPrompt() {
        Toast.makeText(getContext(), getString(R.string.select_a_story_first), Toast.LENGTH_LONG).show();
    }
}
