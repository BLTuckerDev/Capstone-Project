package dev.bltucker.nanodegreecapstone.storydetail;

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
import dev.bltucker.nanodegreecapstone.models.Comment;

public class StoryCommentsAdapter extends RecyclerView.Adapter<StoryCommentsAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    @Inject
    public StoryCommentsAdapter(){
        commentList = new ArrayList<>();
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
        //TODO format the post time
        holder.postTimeTextView.setText("" +comment.getUnixPostTime());
        holder.commentBodyTextView.setText(comment.getCommentText());
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
