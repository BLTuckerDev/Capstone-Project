package dev.bltucker.nanodegreecapstone.storydetail;

import android.support.v7.util.DiffUtil;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;

class DetailStoryDiffCallback extends DiffUtil.Callback{
    private final List<Comment> oldComments;
    private final List<Comment> commentList;

    DetailStoryDiffCallback(List<Comment> oldComments, List<Comment> commentList) {
        this.oldComments = oldComments;
        this.commentList = commentList;
    }

    @Override
    public int getOldListSize() {
        return oldComments.size();
    }

    @Override
    public int getNewListSize() {
        return commentList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Comment oldComment = oldComments.get(oldItemPosition);
        Comment newComment = commentList.get(newItemPosition);

        return oldComment.getCommentId() == newComment.getCommentId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Comment oldComment = oldComments.get(oldItemPosition);
        Comment newComment = commentList.get(newItemPosition);

        return oldComment.getCommentText().equals(newComment.getCommentText());
    }
}
