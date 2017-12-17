package dev.bltucker.nanodegreecapstone.topstories

import android.support.v7.util.DiffUtil

import dev.bltucker.nanodegreecapstone.models.Story

internal class StoryDiffUtilCallback(private val stories: List<Story>,
                                     private val updatedStories: List<Story>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return stories.size
    }

    override fun getNewListSize(): Int {
        return updatedStories.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldStory = stories[oldItemPosition]
        val newStory = updatedStories[newItemPosition]

        return oldStory.id == newStory.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldStory = stories[oldItemPosition]
        val newStory = updatedStories[newItemPosition]

        val titlesAreEqual = oldStory.title != null && newStory.title != null &&
                oldStory.title == newStory.title

        val urlsAreEqual = oldStory.url != null && newStory.url != null &&
                oldStory.url == newStory.url

        return titlesAreEqual && urlsAreEqual
    }
}
