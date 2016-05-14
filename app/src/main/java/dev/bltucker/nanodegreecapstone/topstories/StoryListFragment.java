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
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

public class StoryListFragment extends Fragment implements StoryListView {

    @Bind(R.id.swipe_to_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.story_list_recyclerview)
    RecyclerView recyclerView;

    @Bind(R.id.loading_container)
    LinearLayout loadingContainer;

    @Bind(R.id.content_container)
    LinearLayout contentContainer;

    @Bind(R.id.empty_view_container)
    FrameLayout emptyViewContainer;

    @Inject
    StoryListViewPresenter presenter;

    @Inject
    StoryListAdapter adapter;

    @Inject
    ReadingSession readingSession;

    private Delegate delegate;

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
        loadingContainer.setVisibility(View.INVISIBLE);

        if(readingSession.hasStories()){
            emptyViewContainer.setVisibility(View.GONE);
            contentContainer.setVisibility(View.VISIBLE);
        } else {
            contentContainer.setVisibility(View.INVISIBLE);
            emptyViewContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showStoryDetailView(int storyPosition) {
        if(delegate != null){
            delegate.showCommentsView(storyPosition);
        }
    }

    @Override
    public void showStoryPostUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public interface Delegate {
        void showCommentsView(int storyPosition);
    }
}
