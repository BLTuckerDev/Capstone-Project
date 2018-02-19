package dev.bltucker.nanodegreecapstone.common.data;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.bltucker.nanodegreecapstone.common.models.Comment;
import dev.bltucker.nanodegreecapstone.common.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentDto;
import io.reactivex.Single;
import retrofit2.http.Path;

public class FakeHackerNewsApiService implements HackerNewsApiService {

    private Long[] storyIds;
    private Map<Long, Story> stories;
    private Map<Long, Comment> comments;

    public FakeHackerNewsApiService() {
        storyIds = new Long[0];
        stories = new HashMap<>();
        comments = new HashMap<>();
    }


    public void addFakeData(List<Long> storyIdList, Map<Long, Story> stories, Map<Long, Comment> comments) {
        storyIds = storyIdList.toArray(storyIds);
        this.stories = stories;
        this.comments = comments;
    }

    @Override
    public Single<Long[]> getTopStoryIds() {
        return Single.just(storyIds);
    }

    @Override
    public Single<Story> getStory(@Path("storyId") long storyId) {
        if (stories.containsKey(storyId)) {
            return Single.just(stories.get(storyId));
        } else {
            return Single.error(new Exception("Invalid story id"));
        }
    }

    @Override
    public Single<CommentDto> getComment(@Path("commentId") long commentId) {
        return Single.never();
    }
}
