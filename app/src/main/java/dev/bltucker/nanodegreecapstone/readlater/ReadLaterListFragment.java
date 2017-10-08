package dev.bltucker.nanodegreecapstone.readlater;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.databinding.FragmentReadLaterListBinding;
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;

public class ReadLaterListFragment extends Fragment implements ReadLaterListView {

    private static final String ADAPTER_STORIES_BUNDLE_KEY = "adapterStories";

    @Inject
    ReadLaterListPresenter presenter;

    @Inject
    ReadLaterStoryListAdapter adapter;
    private FragmentReadLaterListBinding binding;

    public ReadLaterListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjector
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
        binding = FragmentReadLaterListBinding.inflate(inflater, container, false);
        binding.swipeToRefreshLayout.setOnRefreshListener(presenter);

        binding.readLaterStoryListRecyclerview.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        binding.readLaterStoryListRecyclerview.setAdapter(adapter);

        if(savedInstanceState != null){
            ArrayList<ReadLaterStory> parcelableArrayList = savedInstanceState.getParcelableArrayList(ADAPTER_STORIES_BUNDLE_KEY);
            if(null == parcelableArrayList || parcelableArrayList.isEmpty()){
                showEmptyView();
            } else {
                showStories(parcelableArrayList);
            }
        }

        return binding.getRoot();
    }

    @Override
    public void showLoadingSpinner() {
        binding.contentContainer.setVisibility(View.INVISIBLE);
        binding.loadingContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingSpinner() {
        binding.loadingContainer.setVisibility(View.INVISIBLE);
        binding.contentContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStories(List<ReadLaterStory> data) {
        binding.swipeToRefreshLayout.setRefreshing(false);
        binding.emptyViewContainer.setVisibility(View.GONE);
        binding.swipeToRefreshLayout.setVisibility(View.VISIBLE);
        adapter.setStories(data);
    }

    @Override
    public void showEmptyView() {
        binding.emptyViewContainer.setVisibility(View.VISIBLE);
        binding.swipeToRefreshLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void readStory(ReadLaterStory story) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(story.getUrl()));
        startActivity(browserIntent);
    }
}
