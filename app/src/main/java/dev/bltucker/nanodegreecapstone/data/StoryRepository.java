package dev.bltucker.nanodegreecapstone.data;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Story;
import io.reactivex.Observable;

public interface StoryRepository {
    Observable<List<Story>> getAllStories();
    void saveStories(Story[] stories);
}
