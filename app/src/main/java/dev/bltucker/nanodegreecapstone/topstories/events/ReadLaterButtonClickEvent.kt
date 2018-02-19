package dev.bltucker.nanodegreecapstone.topstories.events

import dev.bltucker.nanodegreecapstone.common.models.Story
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesFragment

class ReadLaterButtonClickEvent constructor(private val story: Story) : TopStoryClickEvent {
    override fun execute(topStoriesFragment: TopStoriesFragment) {
        topStoriesFragment.showStoryPostUrl(story.url)
    }
}