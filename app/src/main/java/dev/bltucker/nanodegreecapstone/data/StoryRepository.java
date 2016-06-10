package dev.bltucker.nanodegreecapstone.data;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;

public interface StoryRepository {
    Observable<List<Story>> getAllStories();
    Observable<List<Comment>> getStoryComments(long storyId);
    void saveStories(List<Story> stories);
}
