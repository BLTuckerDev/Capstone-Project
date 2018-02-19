package dev.bltucker.nanodegreecapstone.topstories.events

import dev.bltucker.nanodegreecapstone.common.models.Story
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesFragment

class CommentsButtonClickEvent constructor(private val story: Story) : TopStoryClickEvent {
    override fun execute(topStoriesFragment: TopStoriesFragment) {
        topStoriesFragment.showStoryDetailView(story)
    }
}