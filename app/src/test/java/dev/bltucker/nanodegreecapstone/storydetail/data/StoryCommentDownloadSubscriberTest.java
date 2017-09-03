package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.CommentRepository;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContentResolver.class, ContentValues.class, Uri.class, Comment.class, Timber.class })
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class StoryCommentDownloadSubscriberTest {

    CommentRepository commentRepository;

    EventBus mockEventBus;

    StoryCommentDownloadSubscriber objectUnderTest;

    @Before
    public void setup(){
        commentRepository = mock(CommentRepository.class);
        mockEventBus = mock(EventBus.class);
        objectUnderTest = new StoryCommentDownloadSubscriber(commentRepository, mockEventBus);
    }

    @Test
    public void testOnNext_ShouldSaveIntoCommentRepository() throws Exception {
        Comment saveMe = new Comment(1L, 1L, "Author", "SOme Text", System.currentTimeMillis(), 20L, 1);
        objectUnderTest.onNext(saveMe);

        verify(objectUnderTest.commentRepository, times(1)).saveComment(saveMe);
    }

    @Test
    public void testOnNext_WithShouldContinueSetToFalse_ShouldStopDownloading(){
        Comment saveMe = new Comment(1L, 1L, "Author", "SOme Text", System.currentTimeMillis(), 20L, 1);
        Disposable mockDisposable = mock(Disposable.class);

        doNothing().when(mockDisposable).dispose();
        when(mockDisposable.isDisposed()).thenReturn(true);

        objectUnderTest.onSubscribe(mockDisposable);
        objectUnderTest.shouldContinueDownloadingComments = false;
        objectUnderTest.onNext(saveMe);

        assertTrue(objectUnderTest.isUnsubscribed());

    }

    @Test
    public void testOnCompleted_ShouldFireEvent() throws Exception {
        mockStatic(Timber.class);

        PowerMockito.doNothing().when(Timber.class, "d", anyString());

        objectUnderTest.onComplete();

        verify(mockEventBus, times(1)).publish(anyObject());
    }

}