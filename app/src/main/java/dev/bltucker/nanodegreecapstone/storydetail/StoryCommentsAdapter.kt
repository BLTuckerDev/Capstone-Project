package dev.bltucker.nanodegreecapstone.storydetail

import android.content.res.Resources
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.bltucker.nanodegreecapstone.R
import dev.bltucker.nanodegreecapstone.databinding.StoryCommentLayoutItemBinding
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC
import dev.bltucker.nanodegreecapstone.models.Comment
import java.util.*
import javax.inject.Inject

class StoryCommentsAdapter @Inject
constructor(@param:GregorianUTC private val calendar: Calendar, private val resources: Resources) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var comments: Array<Comment>

    init {
        comments = arrayOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = StoryCommentLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setTag(R.id.comment_container, binding.commentContainer)
        return CommentViewHolder(binding, calendar, resources)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (comments.isEmpty()) {
            return
        }

        val comment = comments[position]

        val commentViewHolder = holder as CommentViewHolder
        commentViewHolder.bind(comment)
    }

    override fun getItemViewType(position: Int): Int {
        return COMMENT_ITEM_TYPE
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    internal fun updateComments(updatedComments: Array<Comment>) {
        val diffCallback = DetailStoryDiffCallback(comments, updatedComments)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        comments = updatedComments
        diffResult.dispatchUpdatesTo(this@StoryCommentsAdapter)
    }

    companion object {
        private val COMMENT_ITEM_TYPE = R.layout.story_comment_layout_item
    }

}
