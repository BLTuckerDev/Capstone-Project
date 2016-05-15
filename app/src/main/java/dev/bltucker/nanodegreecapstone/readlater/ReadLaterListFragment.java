package dev.bltucker.nanodegreecapstone.readlater;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;

public class ReadLaterListFragment extends Fragment implements ReadLaterListView {

    private static final String ADAPTER_STORIES_BUNDLE_KEY = "adapterStories";

    @Inject
    ReadLaterListPresenter presenter;

    @Inject
    ReadLaterStoryListAdapter adapter;

    @Bind(R.id.loading_container)
    View loadingContainer;

    @Bind(R.id.content_container)
    View contentContainer;

    @Bind(R.id.empty_view_container)
    View emptyContainer;

    @Bind(R.id.read_later_story_list_recyclerview)
    RecyclerView recyclerView;

    @Bind(R.id.swipe_to_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public ReadLaterListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CapstoneApplication.getApplication()
                .getApplicationComponent()
                .readLaterComponent(new ReadLaterListFragmentModule(this))
                .inject(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ADAPTER_STORIES_BUNDLE_KEY,new ArrayList<ReadLaterStory>(adapter.getStories()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(null == savedInstanceState){
            presenter.onViewCreated(this);
        } else {
            presenter.onViewRestored(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_read_later_list, container, false);
        ButterKnife.bind(this, root);
        swipeRefreshLayout.setOnRefreshListener(presenter);

        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(adapter);

        if(savedInstanceState != null){
            ArrayList<ReadLaterStory> parcelableArrayList = savedInstanceState.getParcelableArrayList(ADAPTER_STORIES_BUNDLE_KEY);
            if(null == parcelableArrayList || parcelableArrayList.isEmpty()){
                showEmptyView();
            } else {
                showStories(parcelableArrayList);
            }
        }

        return root;
    }

    @Override
    public void showLoadingSpinner() {
        contentContainer.setVisibility(View.INVISIBLE);
        loadingContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingSpinner() {
        loadingContainer.setVisibility(View.INVISIBLE);
        contentContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStories(List<ReadLaterStory> data) {
        swipeRefreshLayout.setRefreshing(false);
        emptyContainer.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        adapter.setStories(data);
    }

    @Override
    public void showEmptyView() {
        emptyContainer.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void readStory(ReadLaterStory story) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(story.getUrl()));
        startActivity(browserIntent);
    }
}
