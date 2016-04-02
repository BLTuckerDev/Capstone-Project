package dev.bltucker.nanodegreecapstone.data;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface HackerNewsApiService {

    @GET("topstories.json")
    Observable<List<Long>> getTopStoryIds();

    @GET("item/{storyId}.json")
    Observable<Story> getStory(@Path("storyId") long storyId);

    @GET("item/{commentId}.json")
    Observable<Comment> getComment(@Path("commentId") long commentId);

}
