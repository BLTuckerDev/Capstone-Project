package dev.bltucker.nanodegreecapstone.topstories

import dev.bltucker.nanodegreecapstone.data.StoryRepository
import dev.bltucker.nanodegreecapstone.models.Story
import io.reactivex.observers.TestObserver
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.*

class TopStoriesViewModelTest {

    lateinit var objectUnderTest: TopStoriesViewModel

    @Before
    fun setup() {
        var mockRepository = mock(StoryRepository::class.java)
        val mockRequestDelegate = mock(SyncRequestDelegate::class.java)
        objectUnderTest = TopStoriesViewModel(mockRepository, mockRequestDelegate)
    }

    @Test
    fun testOnCommentsButtonClickEmits() {
        val fakeStory = Story(1L, "Fake Poster", 1L, Date().time, "Fake Title", "FakeUrl")
        val testObserver = TestObserver<Story>()
        objectUnderTest.getObservableCommentClicks().subscribe(testObserver)

        objectUnderTest.onCommentsButtonClick(fakeStory)


        testObserver.assertValue(fakeStory)
        testObserver.assertValueCount(1)

    }


}