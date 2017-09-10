package dev.bltucker.nanodegreecapstone.storydetail;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.common.ApplicationViewModelsFactory;
import dev.bltucker.nanodegreecapstone.databinding.FragmentStoryDetailBinding;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule.DETAIL_STORY_BUNDLE_KEY;
import static dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule.STORY_BUNDLE_KEY;

public class StoryDetailFragment extends Fragment implements LifecycleRegistryOwner {

    @Inject
    StoryCommentsAdapter commentsAdapter;

    @Inject
    LifecycleRegistry lifecycleRegistry;

    @Inject
    StoryCommentsSyncer storyCommentsSyncer;

    @Inject
    ApplicationViewModelsFactory applicationViewModelsFactory;

    @Inject
    @Nullable
    Story story;

    ShareActionProvider shareActionProvider;

    MenuItem shareMenuItem;

    private FragmentStoryDetailBinding binding;
    private StoryDetailViewModel storyDetailViewModel;

    private CompositeDisposable modelSubscriptions = new CompositeDisposable();

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
        if (shareActionProvider != null) {
            setupShareActionProvider();
            shareMenuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_save_story) {
            if(story != null){
                storyDetailViewModel.onSaveStoryClick(story);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupShareActionProvider() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, story.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, story.getUrl());
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        DaggerInjector.getApplicationComponent()
                .storyDetailComponent(new StoryDetailFragmentModule(this, savedInstanceState))
                .inject(this);
        storyDetailViewModel = ViewModelProviders.of(this, applicationViewModelsFactory).get(StoryDetailViewModel.class);
        getLifecycle().addObserver(storyCommentsSyncer);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DETAIL_STORY_BUNDLE_KEY, story);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryDetailBinding.inflate(inflater, container, false);
        binding.headerInclude.readButton.setOnClickListener(v -> showStoryPostUrl());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.commentListRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.commentListRecyclerview.setAdapter(commentsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(story != null){
            showStory();

            storyDetailViewModel.getObservableComments(story.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Comment[]>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            modelSubscriptions.add(d);
                        }

                        @Override
                        public void onNext(Comment[] comments) {
                            commentsAdapter.updateComments(comments);
                        }

                        @Override
                        public void onError(Throwable e) {
                            showGenericError("Error loading story comments");
                        }

                        @Override
                        public void onComplete() {}
                    });

            storyDetailViewModel.getObservableReadLaterSuccessStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            modelSubscriptions.add(d);
                        }

                        @Override
                        public void onNext(Boolean saveSuccess) {
                            if(saveSuccess){
                                Toast.makeText(getContext(), getString(R.string.story_saved), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            showEmptyView();
        }

    }

    private void showGenericError(String errorMessage) {
        Snackbar.make(binding.coordinatorLayout, errorMessage, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void onStop() {
        modelSubscriptions.clear();
        super.onStop();
    }

    public void showStory() {
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }

        if (null == story) {
            return;
        }

        binding.headerInclude.storyTitleTextview.setText(story.getTitle());
        binding.headerInclude.storyUrlTextview.setText(story.getUrl());
        binding.headerInclude.posterNameTextview.setText(String.format(getString(R.string.by_poster), story.getPosterName()));
        binding.headerInclude.scoreTextview.setText(String.format(getString(R.string.story_score), story.getScore()));
        if (null == story.getUrl()) {
            binding.headerInclude.readButton.setVisibility(View.INVISIBLE);
        } else {
            binding.headerInclude.readButton.setVisibility(View.VISIBLE);
        }
    }

    public void showStoryPostUrl() {
        if (story != null && story.getUrl() != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(story.getUrl()));
            startActivity(browserIntent);
        }
    }

    public void showEmptyView() {
        binding.commentListRecyclerview.setVisibility(View.INVISIBLE);
        binding.emptyViewContainer.setVisibility(View.VISIBLE);
        binding.headerInclude.detailHeaderCardview.setVisibility(View.INVISIBLE);
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}
