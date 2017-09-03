package dev.bltucker.nanodegreecapstone.storydetail

import dev.bltucker.nanodegreecapstone.models.Comment

data class StoryDetailModel(val detailStory : DetailStory?,
                            val comments : Array<Comment>?,
                            val isLoading: Boolean,
                            val error: Throwable?)