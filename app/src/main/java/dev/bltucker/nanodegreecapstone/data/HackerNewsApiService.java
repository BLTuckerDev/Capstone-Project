package dev.bltucker.nanodegreecapstone.data;

import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentDto;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HackerNewsApiService {

    @GET("topstories.json")
    Single<Long[]> getTopStoryIds();

    @GET("item/{storyId}.json")
    Single<Story> getStory(@Path("storyId") long storyId);

    @GET("item/{commentId}.json")
    Single<CommentDto> getComment(@Path("commentId") long commentId);
}
