package dev.bltucker.nanodegreecapstone.storydetail;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC;
import dev.bltucker.nanodegreecapstone.models.Comment;

public class StoryCommentsAdapter extends RecyclerView.Adapter<StoryCommentsAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private final Calendar calendar;

    @Inject
    public StoryCommentsAdapter(@GregorianUTC Calendar utcCalendar){
        commentList = new ArrayList<>();
        calendar = utcCalendar;
    }

    public void addComments(List<Comment> comments){
        int oldSize = this.commentList.size();
        this.commentList.addAll(comments);
        this.notifyItemRangeInserted(oldSize, comments.size());
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_comment_layout_item, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.authorNameTextView.setText(comment.getAuthorName());
        long elapsedSeconds = (calendar.getTimeInMillis() / 1000) - comment.getUnixPostTime();
        long elapsedMinutes = elapsedSeconds / 60;
        holder.postTimeTextView.setText(String.format("%d minutes ago", elapsedMinutes));
        holder.commentBodyTextView.setText(Html.fromHtml(comment.getCommentText()));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.comment_poster_name_textview)
        TextView authorNameTextView;

        @Bind(R.id.comment_post_time_textview)
        TextView postTimeTextView;

        @Bind(R.id.comment_body_textview)
        TextView commentBodyTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
