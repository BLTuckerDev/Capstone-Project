package dev.bltucker.nanodegreecapstone.storydetail

import dev.bltucker.nanodegreecapstone.models.Comment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryDetailModelFactory @Inject constructor() {

    fun createLoadingModel(): StoryDetailModel {
        return StoryDetailModel(null, null, true, null)
    }

    fun createErrorModel(error: Throwable): StoryDetailModel {
        return StoryDetailModel(null, null, false, error)
    }

    fun createStoryDetailModel(detailStory: DetailStory, comments: Array<Comment>?): StoryDetailModel {
        return StoryDetailModel(detailStory, comments, false, null)
    }


}