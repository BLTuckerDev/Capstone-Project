package dev.bltucker.nanodegreecapstone.topstories;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.databinding.HeadlineItemLayoutBinding;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.StoryHeadlineViewHolder> {

    private final StoryListViewPresenter presenter;
    private final ReadingSession readingSession;

    @Inject
    StoryListAdapter(StoryListViewPresenter presenter, ReadingSession readingSession){
        this.presenter = presenter;
        this.readingSession = readingSession;
    }

    @Override
    public StoryListAdapter.StoryHeadlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        HeadlineItemLayoutBinding binding = HeadlineItemLayoutBinding.inflate(inflater, parent, false);
        return new StoryHeadlineViewHolder(binding);
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

    void reset(){
        notifyDataSetChanged();
    }


    class StoryHeadlineViewHolder extends RecyclerView.ViewHolder{

        private final HeadlineItemLayoutBinding binding;

        Story story;

        StoryHeadlineViewHolder(HeadlineItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.headlineCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onCommentsButtonClick(story);
                }
            });

            binding.commentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onCommentsButtonClick(story);
                }
            });

            binding.readButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onReadStoryButtonClick(story);
                }
            });
        }

        public void setStory(Story story){
            this.story = story;
            Context viewContext = itemView.getContext();
            binding.storyTitleTextview.setText(story.getTitle());
            binding.posterNameTextview.setText(String.format(viewContext.getString(R.string.by_poster), story.getPosterName()));
            binding.scoreTextview.setText(String.valueOf(story.getScore()));
            if(null == story.getUrl()){
                binding.readButton.setVisibility(View.INVISIBLE);
            } else {
                binding.readButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
