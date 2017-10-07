package dev.bltucker.nanodegreecapstone.storydetail

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.TypedValue
import android.widget.LinearLayout

import java.util.Calendar

import dev.bltucker.nanodegreecapstone.R
import dev.bltucker.nanodegreecapstone.databinding.StoryCommentLayoutItemBinding
import dev.bltucker.nanodegreecapstone.models.Comment

internal class CommentViewHolder(private val binding: StoryCommentLayoutItemBinding,
                                 private val calendar: Calendar,
                                 private val resources: Resources) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: Comment) {
        binding.commentPosterNameTextview.text = comment.getAuthorName()
        binding.commentPostTimeTextview.text = getFormattedCommentTime(comment, binding.root.context)
        binding.commentBodyTextview.text = Html.fromHtml(comment.getCommentText())

        val commentDepth = comment.getDepth()
        val container = binding.root.getTag(R.id.comment_container) as LinearLayout
        val layoutParams = container.layoutParams as RecyclerView.LayoutParams
        val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (commentDepth * MARGIN_PER_DEPTH).toFloat(), resources.displayMetrics)
        layoutParams.marginStart = margin.toInt()
        container.layoutParams = layoutParams
        container.invalidate()

    }

    private fun getFormattedCommentTime(comment: Comment, context: Context): String {
        val elapsedSeconds = calendar.timeInMillis / 1000 - comment.getUnixPostTime()
        val elapsedMinutes = elapsedSeconds / 60
        val elapsedHours = elapsedMinutes / 60
        val elapsedDays = elapsedHours / 24

        if (elapsedDays > 0) {
            return String.format(context.getString(R.string.days_ago), elapsedDays)
        }

        if (elapsedHours > 0) {
            return String.format(context.getString(R.string.hours_ago), elapsedHours)
        }

        return if (elapsedMinutes > 0) {
            String.format(context.getString(R.string.minutes_ago), elapsedMinutes)
        } else context.getString(R.string.less_than_a_minute)

    }

    companion object {
        private val MARGIN_PER_DEPTH = 15
    }
}
