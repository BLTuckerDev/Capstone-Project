package dev.bltucker.nanodegreecapstone.topstories;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.StoryHeadlineViewHolder> {

    private final StoryListViewPresenter presenter;
    private final int maximumStoryCount;
    private final ReadingSession readingSession;

    @Inject
    public StoryListAdapter(StoryListViewPresenter presenter, @StoryMax int maximumStoryCount, ReadingSession readingSession){
        this.presenter = presenter;
        this.maximumStoryCount = maximumStoryCount;
        this.readingSession = readingSession;
    }

    @Override
    public StoryListAdapter.StoryHeadlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.headline_item_layout, parent, false);
        return new StoryHeadlineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoryListAdapter.StoryHeadlineViewHolder holder, int position) {
        Story story = readingSession.getStory(position);
        holder.setStory(story);
    }

    @Override
    public int getItemCount() {
        return readingSession.storyCount();
    }

    public void reset(){
        notifyDataSetChanged();
    }


    class StoryHeadlineViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.story_title_textview)
        TextView titleTextView;

        @Bind(R.id.poster_name_textview)
        TextView posterNameTextView;

        @Bind(R.id.score_textview)
        TextView scoreTextView;

        @Bind(R.id.read_button)
        Button readButton;

        Story story;

        public StoryHeadlineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setStory(Story story){
            this.story = story;
            Context viewContext = itemView.getContext();
            titleTextView.setText(story.getTitle());
            posterNameTextView.setText(String.format(viewContext.getString(R.string.by_poster), story.getPosterName()));
            scoreTextView.setText(String.valueOf(story.getScore()));
            if(null == story.getUrl()){
                readButton.setVisibility(View.INVISIBLE);
            } else {
                readButton.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.comments_button)
        public void onCommentsButtonClick(View v){
            presenter.onCommentsButtonClick(getAdapterPosition());
        }

        @OnClick(R.id.read_button)
        public void onReadButtonClick(View v){
            presenter.onReadStoryButtonClick(getAdapterPosition());
        }
    }
}
