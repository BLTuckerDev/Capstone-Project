package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.StoryCommentsDownloadCompleteEvent;
import dev.bltucker.nanodegreecapstone.models.Comment;
import timber.log.Timber;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContentResolver.class, ContentValues.class, Uri.class, Comment.class, Timber.class })
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class StoryCommentDownloadSubscriberTest {

    ContentResolver mockContentResolver;

    EventBus mockEventBus;

    StoryCommentDownloadSubscriber objectUnderTest;

    @Before
    public void setup(){
        mockContentResolver = mock(ContentResolver.class);
        mockEventBus = mock(EventBus.class);

        objectUnderTest = new StoryCommentDownloadSubscriber(mockContentResolver, mockEventBus);
    }

    @Test
    public void testOnNext_ShouldBulkInsertIntoContentResolver() throws Exception {

        mockStatic(ContentValues.class);
        mockStatic(Comment.class);
        mockStatic(Uri.class);

        ContentValues mockContentValues = mock(ContentValues.class);
        List<Comment> commentList = new ArrayList<>();

        commentList.add(new Comment(1L, "Author Name", "Comment Text", System.currentTimeMillis(), 1000L));
        commentList.add(new Comment(2L, "Another Author Name", "More Comment Text", System.currentTimeMillis(), 2000L));


        PowerMockito.whenNew(ContentValues.class).withNoArguments().thenReturn(mockContentValues);
        PowerMockito.doNothing().when(mockContentValues).put(anyString(), anyLong());
        PowerMockito.doNothing().when(mockContentValues).put(anyString(), anyString());
        PowerMockito.doNothing().when(mockContentValues).put(anyString(), anyString());
        PowerMockito.doNothing().when(mockContentValues).put(anyString(), anyLong());
        PowerMockito.doNothing().when(mockContentValues).put(anyString(), anyLong());
        PowerMockito.when(mockContentResolver.bulkInsert(any(Uri.class), any(ContentValues[].class))).thenReturn(2);

        when(Comment.mapToContentValues(any(Comment.class))).thenReturn(mockContentValues);

        objectUnderTest.onNext(commentList);

        verify(mockContentResolver, times(1)).bulkInsert(any(Uri.class), any(ContentValues[].class));
    }

    @Test
    public void testOnCompleted_ShouldFireEvent() throws Exception {
        mockStatic(Timber.class);

        PowerMockito.doNothing().when(Timber.class, "d", anyString());

        objectUnderTest.onCompleted();

        verify(mockEventBus, times(1)).publish(anyObject());
    }

}