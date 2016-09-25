package dev.bltucker.nanodegreecapstone.data;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentDto;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface HackerNewsApiService {

    @GET("topstories.json")
    Observable<List<Long>> getTopStoryIds();

    @GET("item/{storyId}.json")
    Observable<Story> getStory(@Path("storyId") long storyId);

    @GET("item/{commentId}.json")
    Observable<CommentDto> getComment(@Path("commentId") long commentId);
}
