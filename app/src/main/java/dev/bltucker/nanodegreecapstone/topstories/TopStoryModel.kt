package dev.bltucker.nanodegreecapstone.topstories

import dev.bltucker.nanodegreecapstone.models.Story

class TopStoryModel constructor(val storyList : List<Story>,
                                val isLoading : Boolean,
                                val isRefreshing: Boolean,
                                val wasRefreshing: Boolean,
                                val isError : Boolean){


    override fun toString(): String {
        return "StoryList Size: ${storyList.size}, \n" +
                "isLoading: $isLoading, \n" +
                "isRefreshing: $isRefreshing, \n" +
                "wasRefreshing: $wasRefreshing, \n" +
                "isError: $isError"
    }
}