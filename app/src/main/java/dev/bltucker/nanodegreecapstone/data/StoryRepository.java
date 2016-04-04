package dev.bltucker.nanodegreecapstone.data;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public interface StoryRepository {
//TODO convert these lists to arrays
    //TODO methods for adding comments and stories as well to be used by the sync adapter
    //TODO consider just returning cursors and using cursor adapters + cursor loaders, might make life lots easier
    List<Story> getAllStories();
    List<Comment> getStoryComments(Story story);
}
