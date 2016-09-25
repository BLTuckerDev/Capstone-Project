package dev.bltucker.nanodegreecapstone.storydetail.data;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Observable;
import rx.Subscriber;

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

        objectUnderTest.get(commentIds)
                .subscribe(new Subscriber<List<Comment>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        assertEquals(2, comments.size());
                        assertEquals(1L, comments.get(0).getId());
                        assertEquals(2L, comments.get(1).getId());
                    }
                });


    }

}