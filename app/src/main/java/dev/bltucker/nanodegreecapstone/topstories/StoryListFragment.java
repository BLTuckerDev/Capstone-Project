package dev.bltucker.nanodegreecapstone.topstories;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;

//TODO while the adapter is empty show a spinner so the user knows stories are loading.
public class StoryListFragment extends Fragment implements StoryListView {

    @Bind(R.id.swipe_to_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.story_list_recyclerview)
    RecyclerView recyclerView;

    @Bind(R.id.loading_spinner)
    ProgressBar loadingSpinner;

    @Inject
    StoryListViewPresenter presenter;

    @Inject
    StoryListAdapter adapter;

    @Inject
    Tracker analyticsTracker;

    private Delegate delegate;
    private boolean launchingStoryDetailsView = false;

    public StoryListFragment() {
        // Required empty public constructor
    }

    public static StoryListFragment newInstance() {
        StoryListFragment fragment = new StoryListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
        analyticsTracker.setScreenName("StoryListView");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_story_list, container, false);
        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(presenter);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Delegate){
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        if(null == savedInstanceState){
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
        presenter.onViewPaused(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        if(launchingStoryDetailsView){
            hideLoadingView();
            launchingStoryDetailsView = false;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        presenter.onViewDestroyed(this);
        super.onDestroy();
    }

    @Override
    public void showStories() {
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        adapter.reset();
    }

    @Override
    public void showCommentsView() {
        if(delegate != null){
            launchingStoryDetailsView = true;
            delegate.showCommentsView();
        }
    }

    @Override
    public void showStoryPostUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void showLoadingView() {
        loadingSpinner.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLoadingView() {
        loadingSpinner.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public interface Delegate {
        void showCommentsView();
    }
}
