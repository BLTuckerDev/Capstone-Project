package dev.bltucker.nanodegreecapstone.data;

import retrofit.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface HackerNewsApiService {

    @GET("topstories.json")
    Observable<Long[]> getTopStoryIds();

    @GET("item/{storyId}.json")
    Observable<Story> getStory(@Path("storyId") long storyId);

    @GET("item/{commentId}.json")
    Observable<Comment> getComment(@Path("commentId") long commentId);

}
