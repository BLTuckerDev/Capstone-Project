package dev.bltucker.nanodegreecapstone.storydetail;

import android.annotation.SuppressLint;
import android.content.ContentResolver;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.bltucker.nanodegreecapstone.data.FakeHackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressLint("unchecked")
@SuppressWarnings("unchecked")
public class CommentRepositoryTest {

    CommentRepository objectUnderTest;
    private CommentsDao mockCommentsDao;

    @Before
    public void setUp() throws Exception {
        mockCommentsDao = mock(CommentsDao.class);
        objectUnderTest = new CommentRepository(mockCommentsDao);
    }

    @Test
    public void testSaveCommentSavesToDao(){
        Comment fakeComment = new Comment(1, 2, "Fake Author", "FakeCOmments", System.currentTimeMillis(), 100, 0);
        objectUnderTest.saveComment(fakeComment);
        verify(mockCommentsDao, times(1)).save(fakeComment);
    }

    @Test
    public void testGetLocalCommentsComeFromDao(){
        final long fakeStoryId = 100;

        when(mockCommentsDao.getStoryCommentsFlowable(fakeStoryId)).thenReturn(Flowable.just(getFakeComments()));

        TestObserver testSubscriber = new TestObserver();
        objectUnderTest.getCommentsForStoryId(fakeStoryId).subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);
    }


    private Comment[] getFakeComments(){
        final long fakeStoryId = 100;
        Comment[] fakeComments = new Comment[3];

        for (int i = 0; i < fakeComments.length; i++) {
            fakeComments[i] = new Comment(i, fakeStoryId, "Fake Author", "Fake Comment", System.currentTimeMillis(), fakeStoryId, 0);
        }


        return fakeComments;

    }


}