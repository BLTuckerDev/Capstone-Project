package dev.bltucker.nanodegreecapstone.topstories.events

import dev.bltucker.nanodegreecapstone.topstories.TopStoriesFragment

interface TopStoryClickEvent {

    fun execute(topStoriesFragment: TopStoriesFragment)

}