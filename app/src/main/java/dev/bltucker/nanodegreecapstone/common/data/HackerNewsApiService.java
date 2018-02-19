package dev.bltucker.nanodegreecapstone.common.data;

import android.support.annotation.NonNull;

import dev.bltucker.nanodegreecapstone.common.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentDto;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HackerNewsApiService {

    @NonNull
    @GET("topstories.json")
    Single<Long[]> getTopStoryIds();

    @NonNull
    @GET("item/{storyId}.json")
    Single<Story> getStory(@Path("storyId") long storyId);

    @NonNull
    @GET("item/{commentId}.json")
    Single<CommentDto> getComment(@Path("commentId") long commentId);
}
