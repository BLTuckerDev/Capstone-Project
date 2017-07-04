package dev.bltucker.nanodegreecapstone.topstories;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.databinding.FragmentStoryListBinding;
import dev.bltucker.nanodegreecapstone.events.SyncStatusObserver;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryListFragment extends Fragment implements StoryListView {

    @Inject
    StoryListViewPresenter presenter;

    @Inject
    StoryListAdapter adapter;

    @Inject
    ReadingSession readingSession;

    @Inject
    SyncStatusObserver syncStatusObserver;

    private Delegate delegate;

    private FragmentStoryListBinding binding;

    public StoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjector.getApplicationComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryListBinding.inflate(inflater, container, false);
        binding.swipeToRefreshLayout.setOnRefreshListener(presenter);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Delegate) {
            this.delegate = (Delegate) context;
        }
    }

    @Override
    public void onDetach() {
        this.delegate = null;
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.storyListRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.storyListRecyclerview.setAdapter(adapter);
        if (null == savedInstanceState) {
            presenter.onViewCreated(this, getLoaderManager());
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
        presenter.onViewPaused();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        presenter.onViewDestroyed();
        super.onDestroy();
    }

    @Override
    public void showStories() {
        if (!readingSession.hasStories() && syncStatusObserver.isSyncInProgress()) {
            return;
        }

        hideLoadingSpinner();
        binding.swipeToRefreshLayout.setRefreshing(false);

        if (readingSession.hasStories()) {
            binding.emptyViewContainer.setVisibility(View.GONE);
            binding.storyListRecyclerview.setVisibility(View.VISIBLE);
        } else {
            binding.storyListRecyclerview.setVisibility(View.INVISIBLE);
            binding.emptyViewContainer.setVisibility(View.VISIBLE);
        }
        adapter.reset();
    }

    @Override
    public void showStoryDetailView(Story story) {
        if (delegate != null) {
            delegate.showCommentsView(story);
        }
    }

    @Override
    public void showStoryPostUrl(String url) {
        FragmentActivity activity = this.getActivity();

        if (null == activity) {
            return;
        }

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ResourcesCompat.getColor(activity.getResources(), R.color.colorPrimary, activity.getTheme()));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(url));
    }

    @Override
    public void showLoadingSpinner() {
        binding.contentContainer.setVisibility(View.INVISIBLE);
        binding.loadingContainer.setVisibility(View.VISIBLE);
    }

    private void hideLoadingSpinner() {
        binding.loadingContainer.setVisibility(View.INVISIBLE);
        binding.contentContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUpdatedStoriesNotification() {
        if (null == getView()) {
            return;
        }

        hideLoadingSpinner();
        binding.swipeToRefreshLayout.setRefreshing(false);

        Snackbar.make(getView(), R.string.new_stories_are_available, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.refresh, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onShowRefreshedStories();
                }
            })
            .show();
    }

    public interface Delegate {
        void showCommentsView(Story story);
    }
}
