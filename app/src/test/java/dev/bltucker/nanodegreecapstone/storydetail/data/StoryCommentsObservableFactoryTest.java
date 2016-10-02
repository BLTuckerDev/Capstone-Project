package dev.bltucker.nanodegreecapstone.storydetail.data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StoryCommentsObservableFactoryTest {

    HackerNewsApiService mockHackerNewsService;

    StoryCommentsObservableFactory objectUnderTest;

    @Before
    public void setup(){
        mockHackerNewsService = mock(HackerNewsApiService.class);
        objectUnderTest = new StoryCommentsObservableFactory(mockHackerNewsService);
    }

    @Test
    public void testGet_ShouldReturnListOfComments(){
        CommentDto fakeCommentOne = new CommentDto("Author One", 1L, new long[0], 1000L, "This is the first comment", System.currentTimeMillis());
        CommentDto fakeCommentTwo = new CommentDto("Author Two", 2L, new long[0], 2000L, "This is the second comment", System.currentTimeMillis());
        when(mockHackerNewsService.getComment(anyLong())).thenReturn(Observable.just(fakeCommentOne), Observable.just(fakeCommentTwo));

        final long[] commentIds = new long[]{1L, 2L};

        TestSubscriber<Comment> commentTestSubscriber = new TestSubscriber<>();

        List<Comment> expectedCommentEmitList = new ArrayList<>();

        expectedCommentEmitList.add(new Comment(fakeCommentOne.id, fakeCommentOne.by, fakeCommentOne.text, fakeCommentOne.time, fakeCommentOne.parent, 0));
        expectedCommentEmitList.add(new Comment(fakeCommentTwo.id, fakeCommentTwo.by, fakeCommentTwo.text, fakeCommentTwo.time, fakeCommentTwo.parent, 0));


        objectUnderTest.get(commentIds)
                .subscribe(commentTestSubscriber);

        commentTestSubscriber.assertReceivedOnNext(expectedCommentEmitList);
    }

    @Test
    public void testGet_WithEmptyCommentIdArray_ShouldReturnEmptyObservable(){
        TestSubscriber<Comment> commentTestSubscriber = new TestSubscriber<>();

        final long[] commentIds = new long[0];
        objectUnderTest.get(commentIds)
                .subscribe(commentTestSubscriber);

        commentTestSubscriber.assertNoValues();
    }

}