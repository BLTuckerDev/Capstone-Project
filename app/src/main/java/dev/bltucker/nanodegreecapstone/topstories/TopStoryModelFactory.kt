package dev.bltucker.nanodegreecapstone.topstories

import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.models.Story
import javax.inject.Inject

@ApplicationScope
class TopStoryModelFactory @Inject constructor(){

    fun createLoadingModel(): TopStoryModel {
        return TopStoryModel(emptyList(), true, false, false)
    }

    fun createRefreshingModel() : TopStoryModel{
        return TopStoryModel(emptyList(), false, true, false)
    }

    fun createErrorModel() : TopStoryModel{
        return TopStoryModel(emptyList(), false, false, true)
    }

    fun createTopStoryModelWithStories(stories : List<Story>) : TopStoryModel {
        return TopStoryModel(stories, false, false, false)
    }


}