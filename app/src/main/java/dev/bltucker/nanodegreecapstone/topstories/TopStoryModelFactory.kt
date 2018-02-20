package dev.bltucker.nanodegreecapstone.topstories

import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.common.models.Story
import javax.inject.Inject

@ApplicationScope
class TopStoryModelFactory @Inject constructor() {

    fun createLoadingModel(previousModelStories : List<Story>): TopStoryModel {
        return TopStoryModel(previousModelStories, emptyList(), true, false, false)
    }

    fun createRefreshingModel(previousStoryList : List<Story>, refreshedStories: List<Story>): TopStoryModel {
        return TopStoryModel(previousStoryList, refreshedStories, false, true, false)
    }

    fun createErrorModel(previousStoryList : List<Story>, refreshedStories: List<Story>): TopStoryModel {
        return TopStoryModel(previousStoryList, refreshedStories, false, false, true)
    }

    fun createTopStoryModelWithStories(stories: List<Story>): TopStoryModel {
        return TopStoryModel(stories, emptyList(), false, false, false)
    }

    fun createTopStoryModelWithRefreshedStories(previousModelStories : List<Story>, refreshedStories : List<Story>) : TopStoryModel{
        return TopStoryModel(previousModelStories, refreshedStories, false, false, false)
    }
}