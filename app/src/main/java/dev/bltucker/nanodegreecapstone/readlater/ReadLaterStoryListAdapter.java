package dev.bltucker.nanodegreecapstone.readlater;

import android.support.v7.widget.CardView;
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
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;

public class ReadLaterStoryListAdapter extends RecyclerView.Adapter<ReadLaterStoryListAdapter.ReadLaterStoryViewHolder> {

    private final ReadLaterListPresenter presenter;
    private List<ReadLaterStory> stories;

    @Inject
    public ReadLaterStoryListAdapter(ReadLaterListPresenter presenter){
        this.presenter = presenter;
        stories = new ArrayList<>();
    }

    @Override
    public ReadLaterStoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.read_later_story_item, parent, false);
        return new ReadLaterStoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReadLaterStoryViewHolder holder, int position) {
        ReadLaterStory story = stories.get(position);
        holder.setStory(stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void setStories(List<ReadLaterStory> newStories){
        stories.clear();
        stories.addAll(newStories);
        notifyDataSetChanged();
    }

    public List<ReadLaterStory> getStories(){
        return new ArrayList<>(stories);
    }

    public class ReadLaterStoryViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.read_later_story_item_cardview)
        CardView cardView;

        @Bind(R.id.story_title_textview)
        TextView storyTitleTextView;

        @Bind(R.id.poster_name_textview)
        TextView posterNameTextView;
        private ReadLaterStory story;

        public ReadLaterStoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onReadLaterStoryClicked(story);
                }
            });
        }

        public void setStory(ReadLaterStory story){
            this.story = story;
            storyTitleTextView.setText(story.getTitle());
            posterNameTextView.setText(story.getPosterName());
        }
    }
}
