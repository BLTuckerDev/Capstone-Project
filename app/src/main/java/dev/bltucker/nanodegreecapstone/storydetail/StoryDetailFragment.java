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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.data.InterruptibleDownloadService;

public class StoryDetailFragment extends Fragment implements StoryDetailView {

    static final String STORY_BUNDLE_KEY = "story";

    private static final String DETAIL_STORY_BUNDLE_KEY = "detailStory";

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

    @Bind(R.id.empty_view_container)
    View emptyViewContainer;

    @Bind(R.id.detail_header_cardview)
    CardView headerView;

    @Inject
    StoryCommentsAdapter commentsAdapter;

    @Inject
    StoryDetailViewPresenter presenter;

    @Inject
    DetailStoryProvider detailStoryProvider;

    ShareActionProvider shareActionProvider;
    MenuItem shareMenuItem;

    private DetailStory detailStory;


    public StoryDetailFragment() {
        // Required empty public constructor
    }

    public static StoryDetailFragment newInstance(@Nullable Story selectedStory) {
        StoryDetailFragment storyDetailFragment = new StoryDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(STORY_BUNDLE_KEY, selectedStory);
        storyDetailFragment.setArguments(args);
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

        if (detailStory.hasStory() && shareActionProvider != null) {
            setupShareActionProvider();
            shareMenuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item_save_story) {
            presenter.onSaveStoryClick(detailStory);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupShareActionProvider() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, detailStory.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, detailStory.getUrl());
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        DaggerInjector.getApplicationComponent()
                .storyDetailComponent(new StoryDetailFragmentModule(this))
                .inject(this);

        initializeDetailStory(savedInstanceState);
    }

    private void initializeDetailStory(Bundle savedInstanceState) {
        //TODO HANDLE NULL STORIES FOR TABLET MODE
        if (savedInstanceState != null) {
            detailStory = savedInstanceState.getParcelable(DETAIL_STORY_BUNDLE_KEY);
        } else {
            detailStory = detailStoryProvider.getDetailStory((Story) getArguments().getParcelable(STORY_BUNDLE_KEY), new ArrayList<Comment>());
            InterruptibleDownloadService.startDownload(getContext(), detailStory);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DETAIL_STORY_BUNDLE_KEY, detailStory);
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
        commentsAdapter.setDetailStory(detailStory);
        recyclerView.setAdapter(commentsAdapter);
        if (null == savedInstanceState) {
            presenter.onViewCreated(this, detailStory);
        } else {
            presenter.onViewRestored(this, detailStory);
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
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }

        if (!detailStory.hasStory()) {
            return;
        }

        storyTitleTextView.setText(detailStory.getTitle());
        storyUrlTextView.setText(detailStory.getUrl());
        storyPosterTextView.setText(String.format(getString(R.string.by_poster), detailStory.getPosterName()));
        storyScoreTextView.setText(String.format(getString(R.string.story_score), detailStory.getScore()));
        if (null == detailStory.getUrl()) {
            readButton.setVisibility(View.INVISIBLE);
        } else {
            readButton.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.read_button)
    public void onReadButtonClick(View v) {
        presenter.onReadButtonClicked();
    }

    @Override
    public void showStoryPostUrl() {
        if (detailStory.hasStory() && detailStory.getUrl() != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(detailStory.getUrl()));
            startActivity(browserIntent);
        }
    }

    @Override
    public void showStorySaveConfirmation() {
        Toast.makeText(getContext(), getString(R.string.story_saved), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showEmptyView() {
        recyclerView.setVisibility(View.INVISIBLE);
        emptyViewContainer.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.INVISIBLE);
    }
}
