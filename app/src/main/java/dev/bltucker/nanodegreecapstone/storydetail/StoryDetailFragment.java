package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryDetailFragment extends Fragment implements StoryDetailView {

    @Bind(R.id.story_title_textview)
    TextView storyTitleTextView;

    @Bind(R.id.story_url_textview)
    TextView storyUrlTextView;

    @Bind(R.id.poster_name_textview)
    TextView storyPosterTextView;

    @Bind(R.id.score_textview)
    TextView storyScoreTextView;

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
        return new StoryDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
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
        if(null == savedInstanceState){
            presenter.onViewCreated(this);
        } else {
            presenter.onViewRestored(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResumed(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onViewPaused();
    }

    @Override
    public void onDestroy() {
        presenter.onViewDestroyed();
        super.onDestroy();
    }

    @Override
    public void showStory(Story story) {
        storyTitleTextView.setText(story.getTitle());
        storyUrlTextView.setText(story.getUrl());
        storyPosterTextView.setText(String.format(getString(R.string.by_poster), story.getAuthorName()));
        storyScoreTextView.setText(String.format(getString(R.string.story_score), story.getScore()));
    }

    @Override
    public void showComments(List<Comment> commentList) {
        commentsAdapter.addComments(commentList);
    }

    @OnClick(R.id.read_button)
    public void onReadButtonClick(View v){
        presenter.onReadButtonClicked();
    }

    @Override
    public void showStoryPostUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
