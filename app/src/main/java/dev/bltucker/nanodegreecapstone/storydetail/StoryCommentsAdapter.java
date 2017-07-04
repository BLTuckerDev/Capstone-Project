package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.databinding.StoryCommentLayoutItemBinding;
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.events.DetailStoryChangeEvent;

//TODO need to deal with rotation while loading, make sure we dont reload comments that are already loaded
//TODO when we see that the detail story has updated, see if the loading spinner item is visible, if it is notify(), if it isnt maybe we dont notify until it is?
public class StoryCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Observer {

    private static final int EMPTY_COMMENT_ITEM_TYPE = 1;
    private static final int COMMENT_ITEM_TYPE = 2;
    private static final int LOADING_COMMENTS_ITEM_TYPE = 3;
    private static final int MARGIN_PER_DEPTH = 15;

    private final Resources resources;
    private final Calendar calendar;

    private DetailStory detailStory;

    @Inject
    public StoryCommentsAdapter(@GregorianUTC Calendar utcCalendar, Resources resources){
        this.resources = resources;
        calendar = utcCalendar;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == EMPTY_COMMENT_ITEM_TYPE){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_comments_section, parent, false);
            return new EmptyCommentsViewHolder(itemView);
        } else if(viewType == LOADING_COMMENTS_ITEM_TYPE){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_loading_item, parent, false);
            return new LoadingCommentsViewHolder(itemView);

        } else {
            StoryCommentLayoutItemBinding binding = StoryCommentLayoutItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            binding.getRoot().setTag(R.id.comment_container, binding.commentContainer);
            return new CommentViewHolder(binding, calendar, resources);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == EMPTY_COMMENT_ITEM_TYPE){
            return;
        }

        if(holder.getItemViewType() == LOADING_COMMENTS_ITEM_TYPE){
            return;
        }

        if(!detailStory.hasStory()){
            return;
        }

        Comment comment = detailStory.getComment(position);

        if(null == comment){
            return;
        }

        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        commentViewHolder.bind(comment);
    }

    @Override
    public int getItemViewType(int position) {
        if(!detailStory.hasStory() || detailStory.getCommentCount() == 0){
            return EMPTY_COMMENT_ITEM_TYPE;
        } else if(position == detailStory.getCommentCount()){
            return LOADING_COMMENTS_ITEM_TYPE;
        } else {
            return COMMENT_ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if(detailStory.hasStory()){
            return detailStory.getCommentCount();
        } else {
            return 1;
        }
    }

    void setDetailStory(DetailStory detailStory) {
        if(this.detailStory != null){
            this.detailStory.deleteObserver(this);
        }

        this.detailStory = detailStory;
        this.detailStory.addObserver(this);
        notifyDataSetChanged();
    }

    @Override
    public void update(Observable observable, Object data) {
        final DetailStoryChangeEvent detailStoryChangeEvent = (DetailStoryChangeEvent) data;
        final DetailStoryDiffCallback diffCallback = new DetailStoryDiffCallback(detailStoryChangeEvent.oldComments, detailStoryChangeEvent.commentList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(StoryCommentsAdapter.this);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder{

        private final StoryCommentLayoutItemBinding binding;
        private final Calendar calendar;
        private final Resources resources;

        CommentViewHolder(StoryCommentLayoutItemBinding binding, Calendar calendar, Resources resources) {
            super(binding.getRoot());
            this.binding = binding;
            this.calendar = calendar;
            this.resources = resources;
        }

        public void bind(Comment comment) {
            binding.commentPosterNameTextview.setText(comment.getAuthorName());
            binding.commentPostTimeTextview.setText(getFormattedCommentTime(comment, binding.getRoot().getContext()));
            binding.commentBodyTextview.setText(Html.fromHtml(comment.getCommentText()));

            int commentDepth = comment.getDepth();
            LinearLayout container = (LinearLayout) binding.getRoot().getTag(R.id.comment_container);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) container.getLayoutParams();
            float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, commentDepth * MARGIN_PER_DEPTH, resources.getDisplayMetrics());
            layoutParams.setMarginStart((int) margin);
            container.setLayoutParams(layoutParams);
            container.invalidate();

        }

        @SuppressWarnings("squid:S109")
        private String getFormattedCommentTime(Comment comment, Context context){
            long elapsedSeconds = (calendar.getTimeInMillis() / 1000) - comment.getUnixPostTime();
            long elapsedMinutes = elapsedSeconds / 60;
            long elapsedHours = elapsedMinutes / 60;
            long elapsedDays = elapsedHours / 24;

            if(elapsedDays > 0){
                return String.format(context.getString(R.string.days_ago), elapsedDays);
            }

            if(elapsedHours > 0){
                return String.format(context.getString(R.string.hours_ago), elapsedHours);
            }

            if(elapsedMinutes > 0){
                return String.format(context.getString(R.string.minutes_ago), elapsedMinutes);
            }

            return context.getString(R.string.less_than_a_minute);
        }
    }

    private static class EmptyCommentsViewHolder extends RecyclerView.ViewHolder{
        EmptyCommentsViewHolder(View itemView){
            super(itemView);
        }
    }

    private static class LoadingCommentsViewHolder extends RecyclerView.ViewHolder {
        LoadingCommentsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
