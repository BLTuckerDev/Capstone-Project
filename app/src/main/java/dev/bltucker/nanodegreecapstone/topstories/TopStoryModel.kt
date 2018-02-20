package dev.bltucker.nanodegreecapstone.topstories

import dev.bltucker.nanodegreecapstone.common.models.Story

class TopStoryModel constructor(val storyList: List<Story>,
                                val refreshedStoryList: List<Story>,
                                val isLoading: Boolean,
                                val isRefreshing: Boolean,
                                val isError: Boolean) {
    override fun toString(): String {
        return "TopStoryModel(storyList=$storyList, refreshedStoryList=$refreshedStoryList, isLoading=$isLoading, isRefreshing=$isRefreshing, isError=$isError)"
    }
}