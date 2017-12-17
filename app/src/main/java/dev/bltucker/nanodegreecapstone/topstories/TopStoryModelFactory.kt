package dev.bltucker.nanodegreecapstone.topstories

import android.util.Log
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.models.Story
import javax.inject.Inject

@ApplicationScope
class TopStoryModelFactory @Inject constructor() {

    fun createLoadingModel(lastModel: TopStoryModel?): TopStoryModel {
        Log.d("Factory", "createLoadingModel with: $lastModel")
        Log.d("Factory", Log.getStackTraceString(Throwable()))
        if (lastModel == null) {
            return TopStoryModel(emptyList(), emptyList(), true, false, false)
        }

        return TopStoryModel(lastModel.storyList, emptyList(), true, false, false)
    }

    fun createRefreshingModel(lastModel: TopStoryModel?): TopStoryModel {
        Log.d("Factory", "createRefreshingModel with: $lastModel")
        Log.d("Factory", Log.getStackTraceString(Throwable()))
        if (lastModel == null) {
            return TopStoryModel(emptyList(), emptyList(), false, true, false)
        }

        return TopStoryModel(lastModel.storyList, lastModel.refreshedStoryList, false, true, false)
    }

    fun createErrorModel(lastModel: TopStoryModel?): TopStoryModel {
        Log.d("Factory", "createErrorModel with $lastModel")
        Log.d("Factory", Log.getStackTraceString(Throwable()))
        if (lastModel == null) {
            return TopStoryModel(emptyList(), emptyList(), false, false, true)
        }

        return TopStoryModel(lastModel.storyList, lastModel.storyList, false, false, true)
    }

    fun createTopStoryModelWithStories(stories: List<Story>): TopStoryModel {
        return TopStoryModel(stories, emptyList(), false, false, false)
    }

    fun createTopStoryModelWithRefreshedStories(topStoryModel: TopStoryModel, refreshedStories : List<Story>) : TopStoryModel{
        return TopStoryModel(topStoryModel.storyList, refreshedStories, false, false, false)
    }
}