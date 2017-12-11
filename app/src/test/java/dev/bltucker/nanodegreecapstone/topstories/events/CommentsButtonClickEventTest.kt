package dev.bltucker.nanodegreecapstone.topstories.events

import dev.bltucker.nanodegreecapstone.models.Story
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesFragment
import org.junit.Test
import org.mockito.Mockito.*

class CommentsButtonClickEventTest {

    @Test
    fun testExecuteShowsDetailView() {
        val mockStory: Story = mock(Story::class.java)
        val mockTopStoryFragment = mock(TopStoriesFragment::class.java)

        doNothing().`when`(mockTopStoryFragment).showStoryDetailView(mockStory)

        val objectUnderTest = CommentsButtonClickEvent(mockStory)

        objectUnderTest.execute(mockTopStoryFragment)

        verify(mockTopStoryFragment, times(1)).showStoryDetailView(mockStory)
    }

}