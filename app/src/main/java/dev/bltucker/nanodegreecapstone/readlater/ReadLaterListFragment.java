package dev.bltucker.nanodegreecapstone.readlater;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;

public class ReadLaterListFragment extends Fragment implements ReadLaterListView {

    @Inject
    ReadLaterListPresenter presenter;

    @Bind(R.id.loading_container)
    View loadingContainer;

    @Bind(R.id.content_container)
    View contentContainer;

    @Bind(R.id.empty_view_container)
    View emptyContainer;

    @Bind(R.id.swipe_to_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public ReadLaterListFragment() {
        // Required empty public constructor
    }

    public static ReadLaterListFragment newInstance() {
        return new ReadLaterListFragment();
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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_read_later_list, container, false);
        ButterKnife.bind(this, root);
        swipeRefreshLayout.setOnRefreshListener(presenter);
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
        emptyContainer.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyView() {
        emptyContainer.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
    }
}
