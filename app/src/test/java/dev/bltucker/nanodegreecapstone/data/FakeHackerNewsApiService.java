package dev.bltucker.nanodegreecapstone.data;


import java.util.List;
import java.util.Map;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import retrofit2.http.Path;
import rx.Observable;

public class FakeHackerNewsApiService implements HackerNewsApiService {

    private List<Long> storyIds;
    private Map<Long, Story> stories;
    private Map<Long, Comment> comments;


    public void addFakeData(List<Long> storyIds, Map<Long, Story> stories, Map<Long,Comment> comments){
        this.storyIds = storyIds;
        this.stories = stories;
        this.comments = comments;
    }

    @Override
    public Observable<List<Long>> getTopStoryIds() {
        return Observable.just(storyIds);
    }

    @Override
    public Observable<Story> getStory(@Path("storyId") long storyId) {
        return Observable.just(stories.get(storyId));
    }

    @Override
    public Observable<Comment> getComment(@Path("commentId") long commentId) {
        return Observable.just(comments.get(commentId));
    }
}
