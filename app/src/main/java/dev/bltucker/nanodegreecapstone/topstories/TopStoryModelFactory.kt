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
            return TopStoryModel(emptyList(), true, false, false, false)
        }

        return TopStoryModel(lastModel.storyList, true, false, lastModel.isRefreshing, false)
    }

    fun createRefreshingModel(lastModel: TopStoryModel?): TopStoryModel {
        Log.d("Factory", "createRefreshingModel with: $lastModel")
        Log.d("Factory", Log.getStackTraceString(Throwable()))
        if (lastModel == null) {
            return TopStoryModel(emptyList(), false, true, false, false)
        }

        return TopStoryModel(lastModel.storyList, false, true,  lastModel.isRefreshing, false)
    }

    fun createErrorModel(lastModel: TopStoryModel?): TopStoryModel {
        Log.d("Factory", "createErrorModel with $lastModel")
        Log.d("Factory", Log.getStackTraceString(Throwable()))
        if (lastModel == null) {
            return TopStoryModel(emptyList(), false, false, false, true)
        }

        return TopStoryModel(lastModel.storyList, false, false, lastModel.isRefreshing, true)
    }

    fun createTopStoryModelWithStories(stories: List<Story>, wasRefreshing: Boolean): TopStoryModel {
        Log.d("Factory", "createTopStoryModelWithStories with storysize: ${stories.size} and wasRefreshing: $wasRefreshing")
        Log.d("Factory", Log.getStackTraceString(Throwable()))
        return TopStoryModel(stories, false, false, wasRefreshing, false)
    }

    fun createTopStoryModelWithWasRefreshingReset(topStoryModel: TopStoryModel) : TopStoryModel {
        Log.d("Factory", "createTopStoryModelWithWasRefreshingReset with $topStoryModel")
        Log.d("Factory", Log.getStackTraceString(Throwable()))
        return TopStoryModel(topStoryModel.storyList, topStoryModel.isLoading, topStoryModel.isRefreshing, false, topStoryModel.isError)
    }
}