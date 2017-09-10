package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.databinding.FragmentStoryDetailBinding;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule;

public class StoryDetailFragment extends Fragment implements StoryDetailView {

    static final String STORY_BUNDLE_KEY = "story";

    private static final String DETAIL_STORY_BUNDLE_KEY = "detailStory";

    @Inject
    StoryCommentsAdapter commentsAdapter;

    @Inject
    StoryDetailViewPresenter presenter;

    @Inject
    DetailStoryProvider detailStoryProvider;

    ShareActionProvider shareActionProvider;
    MenuItem shareMenuItem;

    private DetailStory detailStory;
    private FragmentStoryDetailBinding binding;


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
            if(detailStory.hasStory()){
                //TODO kick off comment sync?
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DETAIL_STORY_BUNDLE_KEY, detailStory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryDetailBinding.inflate(inflater, container, false);
        binding.headerInclude.readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onReadButtonClicked();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.commentListRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsAdapter.setDetailStory(detailStory);
        binding.commentListRecyclerview.setAdapter(commentsAdapter);
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

        binding.headerInclude.storyTitleTextview.setText(detailStory.getTitle());
        binding.headerInclude.storyUrlTextview.setText(detailStory.getUrl());
        binding.headerInclude.posterNameTextview.setText(String.format(getString(R.string.by_poster), detailStory.getPosterName()));
        binding.headerInclude.scoreTextview.setText(String.format(getString(R.string.story_score), detailStory.getScore()));
        if (null == detailStory.getUrl()) {
            binding.headerInclude.readButton.setVisibility(View.INVISIBLE);
        } else {
            binding.headerInclude.readButton.setVisibility(View.VISIBLE);
        }
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
        binding.commentListRecyclerview.setVisibility(View.INVISIBLE);
        binding.emptyViewContainer.setVisibility(View.VISIBLE);
        binding.headerInclude.detailHeaderCardview.setVisibility(View.INVISIBLE);
    }
}
