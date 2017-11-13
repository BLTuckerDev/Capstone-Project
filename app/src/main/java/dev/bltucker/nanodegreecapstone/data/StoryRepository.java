package dev.bltucker.nanodegreecapstone.data;

import android.support.annotation.NonNull;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Story;
import io.reactivex.Observable;


public interface StoryRepository {

    @NonNull
    Observable<List<Story>> getAllStories();

    void saveStories(Story[] stories);
}
