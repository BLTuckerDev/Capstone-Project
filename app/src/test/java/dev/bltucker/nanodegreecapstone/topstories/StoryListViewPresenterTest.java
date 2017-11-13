package dev.bltucker.nanodegreecapstone.topstories;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoryListViewPresenterTest {

    StoryListViewPresenter objectUnderTest;
    private ReadingSession mockReadingSession;
    private StoryListLoaderCallbackDelegate mockCallbackDelegate;
    private SyncRequestDelegate mockSyncRequestDelegate;

    private StoryListView mockStoryListView;

    @Before
    public void setUp() throws Exception {

        mockStoryListView = mock(StoryListView.class);
        mockReadingSession = mock(ReadingSession.class);
        mockCallbackDelegate = mock(StoryListLoaderCallbackDelegate.class);
        mockSyncRequestDelegate = mock(SyncRequestDelegate.class);

        objectUnderTest = new StoryListViewPresenter(mockReadingSession, mockCallbackDelegate, mockSyncRequestDelegate);

    }

    @Test
    public void testOnViewCreated() throws Exception {
        LoaderManager mockLoaderManager = mock(LoaderManager.class);

        objectUnderTest.onViewCreated(mockStoryListView, mockLoaderManager);

        assertEquals(mockStoryListView, objectUnderTest.storyListView);
        assertEquals(mockLoaderManager, objectUnderTest.loaderManager);

        verify(mockCallbackDelegate, times(1)).setStoryListView(mockStoryListView);
    }

    @Test
    public void testOnViewRestored() throws Exception {

        LoaderManager mockLoaderManager = mock(LoaderManager.class);

        objectUnderTest.onViewRestored(mockStoryListView, mockLoaderManager);

        assertEquals(mockStoryListView, objectUnderTest.storyListView);
        assertEquals(mockLoaderManager, objectUnderTest.loaderManager);

        verify(mockCallbackDelegate, times(1)).setStoryListView(mockStoryListView);

    }

    @Test
    public void testOnViewResumedWithNoStories() throws Exception {
        LoaderManager mockLoaderManager = mock(LoaderManager.class);
        Loader mockLoader = mock(Loader.class);

        when(mockReadingSession.hasStories()).thenReturn(false);
        doNothing().when(mockStoryListView).showLoadingSpinner();
        when(mockLoaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, mockCallbackDelegate))
                .thenReturn(mockLoader);

        objectUnderTest.loaderManager = mockLoaderManager;

        objectUnderTest.onViewResumed(mockStoryListView);

        assertEquals(mockStoryListView, objectUnderTest.storyListView);
        verify(mockStoryListView, times(1)).showLoadingSpinner();
        verify(mockLoaderManager, times(1)).initLoader(StoryListLoader.STORY_LIST_LOADER, null, mockCallbackDelegate);

    }

    @Test
    public void testOnViewResumedWithStoriesAndCleanStoryList(){

        when(mockReadingSession.hasStories()).thenReturn(true);
        doNothing().when(mockStoryListView).showStories();

        objectUnderTest.onViewResumed(mockStoryListView);

        verify(mockStoryListView, times(1)).showStories();

    }

    @Test
    public void testOnViewResumedWithStoriesAndDirtyStoryList(){
        LoaderManager mockLoaderManager = mock(LoaderManager.class);
        Loader mockLoader = mock(Loader.class);

        when(mockReadingSession.hasStories()).thenReturn(true);
        doNothing().when(mockStoryListView).showStories();
        when(mockReadingSession.isStoryListIsDirty()).thenReturn(true);
        when(mockLoaderManager.initLoader(StoryListLoader.STORY_LIST_LOADER, null, mockCallbackDelegate))
                .thenReturn(mockLoader);

        objectUnderTest.loaderManager = mockLoaderManager;

        objectUnderTest.onViewResumed(mockStoryListView);

        verify(mockStoryListView, times(1)).showStories();
        verify(mockLoaderManager, times(1)).initLoader(StoryListLoader.STORY_LIST_LOADER, null, mockCallbackDelegate);
    }

    @Test
    public void testOnViewPaused() throws Exception {
        objectUnderTest.storyListView = mockStoryListView;

        objectUnderTest.onViewPaused();

        assertNull(objectUnderTest.storyListView);
    }

    @Test
    public void testOnViewDestroyed() throws Exception {
        LoaderManager mockLoaderManager = mock(LoaderManager.class);

        objectUnderTest.loaderManager = mockLoaderManager;
        objectUnderTest.onViewDestroyed();

        assertNull(objectUnderTest.storyListView);
        assertNull(objectUnderTest.loaderManager);

    }

    @Test
    public void testOnCommentsButtonClick() throws Exception {
        Story mockStory = mock(Story.class);
        doNothing().when(mockStoryListView).showStoryDetailView(mockStory);

        objectUnderTest.storyListView = mockStoryListView;
        objectUnderTest.onCommentsButtonClick(mockStory);

        verify(mockStoryListView, times(1)).showStoryDetailView(mockStory);
    }

    @Test
    public void testOnReadStoryButtonClick() throws Exception {
        Story mockStory = mock(Story.class);
        when(mockStory.getUrl()).thenReturn("https://google.com/");
        doNothing().when(mockStoryListView).showStoryPostUrl(anyString());

        objectUnderTest.storyListView = mockStoryListView;
        objectUnderTest.onReadStoryButtonClick(mockStory);

        verify(mockStory, times(1)).getUrl();
        verify(mockStoryListView, times(1)).showStoryPostUrl(anyString());
    }

    @Test
    public void testOnRefresh() throws Exception {
        doNothing().when(mockSyncRequestDelegate).sendSyncRequest();
        objectUnderTest.onRefresh();

        verify(mockSyncRequestDelegate, times(1)).sendSyncRequest();
    }

    @Test
    public void testOnShowRefreshedStories() throws Exception {
        doNothing().when(mockReadingSession).updateUserStoriesToLatestSync();
        doNothing().when(mockStoryListView).showStories();

        objectUnderTest.storyListView = mockStoryListView;
        objectUnderTest.onShowRefreshedStories();

        verify(mockReadingSession, times(1)).updateUserStoriesToLatestSync();
        verify(mockStoryListView, times(1)).showStories();
    }

}