package dev.bltucker.nanodegreecapstone.topstories

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService
import dev.bltucker.nanodegreecapstone.models.Story
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.observers.TestObserver
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*

class StoryIdToStoryTransformerTest{

    lateinit var objectUnderTest : StoryIdToStoryTransformer

    lateinit var mockHackerNews : HackerNewsApiService

    @Before
    fun setup(){
        mockHackerNews = mock(HackerNewsApiService::class.java)
        objectUnderTest = StoryIdToStoryTransformer(mockHackerNews, 3)
    }


    @Test
    fun transformerShouldTurnIdsIntoStoryObjects(){

        val storyIdArray = arrayOf(1L, 2L, 3L, 4L, 5L)
        val fakeStoryIdSingle = Single.just(storyIdArray)
        val testSubscriber = TestObserver<List<Story>>()

        Mockito.`when`(mockHackerNews.getStory(1L)).thenReturn(createFakeStory(1L))
        Mockito.`when`(mockHackerNews.getStory(2L)).thenReturn(createFakeStory(2L))
        Mockito.`when`(mockHackerNews.getStory(3L)).thenReturn(createFakeStory(3L))

        val transformedSingle = objectUnderTest.apply(fakeStoryIdSingle)

        transformedSingle.subscribe(testSubscriber)


        verify(mockHackerNews, times(3)).getStory(ArgumentMatchers.anyLong())
        testSubscriber.assertValueCount(1)
        val storyList = testSubscriber.values()[0]

        assertEquals(3, storyList.size)
        assertEquals(1L, storyList[0].id)
        assertEquals(2L, storyList[1].id)
        assertEquals(3L, storyList[2].id)

    }

    private fun createFakeStory(storyId: Long): Single<Story> {
        return Single.just(Story(storyId,
                UUID.randomUUID().toString(),
                100L,
                Date().time,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()))
    }


}