package dev.bltucker.nanodegreecapstone.home;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class HomeViewPresenterTest {

    HomeViewPresenter objectUnderTest;

    HomeView mockView;

    @Before
    public void setup() {
        mockView = mock(HomeView.class);
        objectUnderTest = new HomeViewPresenter();
    }

    @Test
    public void testOnViewCreated() throws Exception {
        objectUnderTest.onViewCreated(mockView);

        assertEquals(objectUnderTest.view, mockView);
    }

    @Test
    public void testOnViewRestored() throws Exception {

        objectUnderTest.onViewRestored(mockView);

        assertEquals(mockView, objectUnderTest.view);
    }

    @Test
    public void testOnViewDestroyed() throws Exception {

        objectUnderTest.view = mockView;
        objectUnderTest.onViewDestroyed();

        assertNull(objectUnderTest.view);
    }

    @Test
    public void testOnShowReadLaterMenuClick() throws Exception {

        objectUnderTest.view = mockView;
        objectUnderTest.onShowReadLaterMenuClick();

        verify(mockView, times(1)).showReadLaterListView();
    }

    @Test
    public void testOnShowReadLater_WithNullView_ShouldNotExecuteViewCommand() {
        objectUnderTest.view = null;
        objectUnderTest.onShowReadLaterMenuClick();

        verify(mockView, never()).showReadLaterListView();
    }
}