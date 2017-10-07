package dev.bltucker.nanodegreecapstone.storydetail

import android.support.v7.util.DiffUtil

import dev.bltucker.nanodegreecapstone.models.Comment

internal class DetailStoryDiffCallback(private val oldComments: Array<Comment>,
                                       private val updatedComments: Array<Comment>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldComments.size
    }

    override fun getNewListSize(): Int {
        return updatedComments.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldComment = oldComments[oldItemPosition]
        val newComment = updatedComments[newItemPosition]

        return oldComment.getCommentId() == newComment.getCommentId()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldComment = oldComments[oldItemPosition]
        val newComment = updatedComments[newItemPosition]

        return oldComment.getCommentText() == newComment.getCommentText()
    }
}
