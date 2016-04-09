package dev.bltucker.nanodegreecapstone.storydetail;


import android.os.Bundle;
import android.support.annotation.Nullable;
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
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryDetailFragment extends Fragment implements StoryDetailView {

    @Bind(R.id.comment_list_recyclerview)
    RecyclerView recyclerView;

    @Inject
    StoryCommentsAdapter commentsAdapter;

    @Inject
    StoryDetailViewPresenter presenter;

    public StoryDetailFragment() {
        // Required empty public constructor
    }

    public static StoryDetailFragment newInstance() {
        StoryDetailFragment fragment = new StoryDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);

        if(null == savedInstanceState){
            presenter.onViewCreated(this);
        } else {
            presenter.onViewRestored(this);
        }
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
        recyclerView.setAdapter(commentsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResumed(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onViewPaused(this);
    }

    @Override
    public void showStory(Story story) {

    }

    @Override
    public void showComments(List<Comment> commentList) {

    }
}
