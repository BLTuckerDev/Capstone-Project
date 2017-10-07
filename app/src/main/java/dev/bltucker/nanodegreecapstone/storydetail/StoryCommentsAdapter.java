package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.res.Resources;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.databinding.StoryCommentLayoutItemBinding;
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC;
import dev.bltucker.nanodegreecapstone.models.Comment;

public class StoryCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int COMMENT_ITEM_TYPE = 2;

    private final Resources resources;
    private final Calendar calendar;

    private Comment[] comments;

    @Inject
    public StoryCommentsAdapter(@GregorianUTC Calendar utcCalendar, Resources resources) {
        this.resources = resources;
        calendar = utcCalendar;
        comments = new Comment[0];
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StoryCommentLayoutItemBinding binding = StoryCommentLayoutItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.getRoot().setTag(R.id.comment_container, binding.commentContainer);
        return new CommentViewHolder(binding, calendar, resources);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (comments.length == 0) {
            return;
        }

        Comment comment = comments[position];

        if (null == comment) {
            return;
        }

        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        commentViewHolder.bind(comment);
    }

    @Override
    public int getItemViewType(int position) {
        return COMMENT_ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        return comments.length;
    }

    void updateComments(Comment[] updatedComments) {
        final DetailStoryDiffCallback diffCallback = new DetailStoryDiffCallback(comments, updatedComments);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        comments = updatedComments;
        diffResult.dispatchUpdatesTo(StoryCommentsAdapter.this);
    }

}
