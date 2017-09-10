package dev.bltucker.nanodegreecapstone.storydetail;

import android.support.v7.util.DiffUtil;

import dev.bltucker.nanodegreecapstone.models.Comment;

class DetailStoryDiffCallback extends DiffUtil.Callback{
    private final Comment[] oldComments;
    private final Comment[] updatedComments;

    DetailStoryDiffCallback(Comment[] oldComments, Comment[] updatedComments) {
        this.oldComments = oldComments;
        this.updatedComments = updatedComments;
    }

    @Override
    public int getOldListSize() {
        return oldComments.length;
    }

    @Override
    public int getNewListSize() {
        return updatedComments.length;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Comment oldComment = oldComments[oldItemPosition];
        Comment newComment = updatedComments[newItemPosition];

        return oldComment.getCommentId() == newComment.getCommentId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Comment oldComment = oldComments[oldItemPosition];
        Comment newComment = updatedComments[newItemPosition];

        return oldComment.getCommentText().equals(newComment.getCommentText());
    }
}
