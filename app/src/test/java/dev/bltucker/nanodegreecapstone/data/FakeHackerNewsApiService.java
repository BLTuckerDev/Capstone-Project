package dev.bltucker.nanodegreecapstone.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentDto;
import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.http.Path;

public class FakeHackerNewsApiService implements HackerNewsApiService {

    private List<Long> storyIds;
    private Map<Long, Story> stories;
    private Map<Long, Comment> comments;

    public FakeHackerNewsApiService(){
        storyIds = new ArrayList<>();
        stories = new HashMap<>();
        comments = new HashMap<>();
    }


    public void addFakeData(List<Long> storyIds, Map<Long, Story> stories, Map<Long,Comment> comments){
        this.storyIds = storyIds;
        this.stories = stories;
        this.comments = comments;
    }

    @Override
    public Single<List<Long>> getTopStoryIds() {
        return Single.just(storyIds);
    }

    @Override
    public Single<Story> getStory(@Path("storyId") long storyId) {
        if(stories.containsKey(storyId)){
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
