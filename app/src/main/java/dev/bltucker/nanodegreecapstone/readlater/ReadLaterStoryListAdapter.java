package dev.bltucker.nanodegreecapstone.readlater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.databinding.ReadLaterStoryItemBinding;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;

public class ReadLaterStoryListAdapter extends RecyclerView.Adapter<ReadLaterStoryListAdapter.ReadLaterStoryViewHolder> {

    private final ReadLaterListPresenter presenter;
    private List<ReadLaterStory> stories;

    @Inject
    public ReadLaterStoryListAdapter(ReadLaterListPresenter presenter) {
        this.presenter = presenter;
        stories = new ArrayList<>();
    }

    @Override
    public ReadLaterStoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReadLaterStoryItemBinding binding = ReadLaterStoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReadLaterStoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ReadLaterStoryViewHolder holder, int position) {
        holder.setStory(stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void setStories(List<ReadLaterStory> newStories) {
        stories.clear();
        stories.addAll(newStories);
        notifyDataSetChanged();
    }

    public List<ReadLaterStory> getStories() {
        return new ArrayList<>(stories);
    }

    class ReadLaterStoryViewHolder extends RecyclerView.ViewHolder {

        private final ReadLaterStoryItemBinding binding;

        private ReadLaterStory story;

        ReadLaterStoryViewHolder(ReadLaterStoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.readButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onReadLaterStoryClicked(story);
                }
            });

            binding.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onReadLaterStoryDeleteClicked(story);
                }
            });

        }

        public void setStory(ReadLaterStory story) {
            this.story = story;
            binding.storyTitleTextview.setText(story.getTitle());
            binding.posterNameTextview.setText(story.getPosterName());
        }
    }
}
