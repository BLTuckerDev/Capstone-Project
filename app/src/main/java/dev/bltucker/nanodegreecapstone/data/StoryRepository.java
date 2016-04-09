package dev.bltucker.nanodegreecapstone.data;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;

public interface StoryRepository {
//TODO convert these lists to arrays
    //TODO methods for adding comments and stories as well to be used by the sync adapter
    //TODO consider just returning cursors and using cursor adapters + cursor loaders, might make life lots easier
    //TODO remember to clear caches when we add the add methods
    Observable<List<Story>> getAllStories();
    Observable<List<Comment>> getStoryComments(Story story);
    void saveStories(List<Story> stories);
}
