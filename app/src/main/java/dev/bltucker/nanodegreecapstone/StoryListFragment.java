package dev.bltucker.nanodegreecapstone;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.bltucker.nanodegreecapstone.models.Story;


public class StoryListFragment extends Fragment implements StoryListView {

    @Bind(R.id.story_list_recyclerview)
    RecyclerView recyclerView;

    @Inject
    StoryListFragmentPresenter presenter;

    @Inject
    StoryListAdapter adapter;

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
        presenter.onViewCreated(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_story_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showStories(List<Story> stories) {
        adapter.setData(stories);
    }
}
