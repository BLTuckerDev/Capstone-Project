package dev.bltucker.nanodegreecapstone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.StoryHeadlineViewHolder> {

    private final StoryListFragmentPresenter presenter;

    private final List<Story> storyList;

    @Inject
    public StoryListAdapter(StoryListFragmentPresenter presenter, @StoryMax int maximumStoryCount){
        this.presenter = presenter;
        storyList = new ArrayList<>(maximumStoryCount);
    }


    @Override
    public StoryListAdapter.StoryHeadlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.headline_item_layout, parent, false);
        return new StoryHeadlineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoryListAdapter.StoryHeadlineViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.titleTextView.setText(story.getTitle());
        holder.posterNameTextView.setText(story.getAuthorName());
        holder.pointsTextView.setText(String.valueOf(story.getScore()));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public void setData(List<Story> stories){
        storyList.clear();
        storyList.addAll(stories);
        notifyDataSetChanged();
    }


    public static class StoryHeadlineViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.story_title_textview)
        TextView titleTextView;

        @Bind(R.id.poster_name_textview)
        TextView posterNameTextView;

        @Bind(R.id.points_textview)
        TextView pointsTextView;

        public StoryHeadlineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.comments_button)
        public void onCommentsButtonClick(View v){

        }

        @OnClick(R.id.read_button)
        public void onReadButtonClick(View v){

        }
    }
}
