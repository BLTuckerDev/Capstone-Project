package dev.bltucker.nanodegreecapstone.storydetail.data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.models.Comment;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

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
        when(mockHackerNewsService.getComment(anyLong())).thenReturn(Single.just(fakeCommentOne), Single.just(fakeCommentTwo));

        final long[] commentIds = new long[]{1L, 2L};

        TestObserver<Comment> commentTestSubscriber = new TestObserver<>();

        List<Comment> expectedCommentEmitList = new ArrayList<>();

        expectedCommentEmitList.add(new Comment(1L, fakeCommentOne.id, fakeCommentOne.by, fakeCommentOne.text, fakeCommentOne.time, fakeCommentOne.parent, 0));
        expectedCommentEmitList.add(new Comment(1L, fakeCommentTwo.id, fakeCommentTwo.by, fakeCommentTwo.text, fakeCommentTwo.time, fakeCommentTwo.parent, 0));


        objectUnderTest.get(1L, commentIds)
                .subscribe(commentTestSubscriber);

        commentTestSubscriber.assertValues(expectedCommentEmitList.get(0), expectedCommentEmitList.get(1));
    }

    @Test
    public void testGet_WithEmptyCommentIdArray_ShouldReturnEmptyObservable(){
        TestObserver<Comment> commentTestSubscriber = new TestObserver<>();

        final long[] commentIds = new long[0];
        objectUnderTest.get(1L, commentIds)
                .subscribe(commentTestSubscriber);

        commentTestSubscriber.assertNoValues();
    }

}