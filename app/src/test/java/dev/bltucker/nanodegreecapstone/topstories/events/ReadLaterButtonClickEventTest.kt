package dev.bltucker.nanodegreecapstone.topstories.events

import dev.bltucker.nanodegreecapstone.models.Story
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesFragment
import org.junit.Test
import org.mockito.Mockito

class ReadLaterButtonClickEventTest{


    @Test
    fun textExecuteShowsStoryPostUrl(){
        val fakeUrl = "fakeUrl"
        val mockStory = Story(1, "", 1L, 1L, "", fakeUrl)
        val mockTopStoryFragment = Mockito.mock(TopStoriesFragment::class.java)

        Mockito.doNothing().`when`(mockTopStoryFragment).showStoryDetailView(mockStory)

        val objectUnderTest = ReadLaterButtonClickEvent(mockStory)

        objectUnderTest.execute(mockTopStoryFragment)

        Mockito.verify(mockTopStoryFragment, Mockito.times(1)).showStoryPostUrl(fakeUrl)

    }

}