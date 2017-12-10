package dev.bltucker.nanodegreecapstone.topstories;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.databinding.HeadlineItemLayoutBinding;
import dev.bltucker.nanodegreecapstone.models.Story;

@ApplicationScope
public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.StoryHeadlineViewHolder> {

    private final TopStoriesViewModel topStoriesViewModel;

    private final List<Story> stories;

    @Inject
    StoryListAdapter(TopStoriesViewModel topStoriesViewModel) {
        this.topStoriesViewModel = topStoriesViewModel;
        stories = new ArrayList<>();
    }

    @Override
    public StoryListAdapter.StoryHeadlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        HeadlineItemLayoutBinding binding = HeadlineItemLayoutBinding.inflate(inflater, parent, false);
        return new StoryHeadlineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(StoryListAdapter.StoryHeadlineViewHolder holder, int position) {
        Story story = stories.get(position);
        holder.setStory(story);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void updateStories(List<Story> updatedStories) {
        //TODO DiffUtil
        this.stories.clear();
        this.stories.addAll(updatedStories);
        notifyDataSetChanged();
    }


    class StoryHeadlineViewHolder extends RecyclerView.ViewHolder {

        private final HeadlineItemLayoutBinding binding;

        Story story;

        StoryHeadlineViewHolder(HeadlineItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.headlineCardView.setOnClickListener(v -> topStoriesViewModel.onCommentsButtonClick(story));
            binding.commentsButton.setOnClickListener(v -> topStoriesViewModel.onCommentsButtonClick(story));
            binding.readButton.setOnClickListener(v -> topStoriesViewModel.onReadLaterStoryButtonClick(story));
        }

        public void setStory(Story story) {
            this.story = story;
            Context viewContext = itemView.getContext();
            binding.storyTitleTextview.setText(story.getTitle());
            binding.posterNameTextview.setText(String.format(viewContext.getString(R.string.by_poster), story.getPosterName()));
            binding.scoreTextview.setText(String.valueOf(story.getScore()));
            if (null == story.getUrl()) {
                binding.readButton.setVisibility(View.INVISIBLE);
            } else {
                binding.readButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
