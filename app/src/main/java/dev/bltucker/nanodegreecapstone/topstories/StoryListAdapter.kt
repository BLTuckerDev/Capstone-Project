package dev.bltucker.nanodegreecapstone.topstories

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.bltucker.nanodegreecapstone.R
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.databinding.HeadlineItemLayoutBinding
import dev.bltucker.nanodegreecapstone.common.models.Story
import java.util.*
import javax.inject.Inject

@ApplicationScope
class StoryListAdapter @Inject
internal constructor(private val topStoriesViewModel: TopStoriesViewModel) : RecyclerView.Adapter<StoryListAdapter.StoryHeadlineViewHolder>() {

    private val stories: MutableList<Story>

    init {
        stories = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryListAdapter.StoryHeadlineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HeadlineItemLayoutBinding.inflate(inflater, parent, false)
        return StoryHeadlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryListAdapter.StoryHeadlineViewHolder, position: Int) {
        val story = stories[position]
        holder.bindStory(story)
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    fun updateStories(updatedStories: List<Story>) {

        val diffResult = DiffUtil.calculateDiff(StoryDiffUtilCallback(this.stories, updatedStories))

        this.stories.clear()
        this.stories.addAll(updatedStories)
        diffResult.dispatchUpdatesTo(this)
    }


    inner class StoryHeadlineViewHolder(private val binding: HeadlineItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        lateinit var story: Story

        init {
            binding.headlineCardView.setOnClickListener { _ -> topStoriesViewModel.onCommentsButtonClick(story) }
            binding.commentsButton.setOnClickListener { _ -> topStoriesViewModel.onCommentsButtonClick(story) }
            binding.readButton.setOnClickListener { _ -> topStoriesViewModel.onReadLaterStoryButtonClick(story) }
        }

        fun bindStory(story: Story) {
            this.story = story
            val viewContext = itemView.context
            binding.storyTitleTextview.text = story.getTitle()
            binding.posterNameTextview.text = String.format(viewContext.getString(R.string.by_poster), story.getPosterName())
            binding.scoreTextview.text = story.getScore().toString()
            if (null == story.getUrl()) {
                binding.readButton.visibility = View.INVISIBLE
            } else {
                binding.readButton.visibility = View.VISIBLE
            }
        }
    }

}
