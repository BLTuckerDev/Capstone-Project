package dev.bltucker.nanodegreecapstone.topstories

import dev.bltucker.nanodegreecapstone.models.Story

class TopStoryModel constructor(val storyList: List<Story>,
                                val refreshedStoryList: List<Story>,
                                val isLoading: Boolean,
                                val isRefreshing: Boolean,
                                val isError: Boolean) {

}