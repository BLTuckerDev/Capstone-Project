package dev.bltucker.nanodegreecapstone.common.data;

import android.support.annotation.NonNull;

import java.util.List;

import dev.bltucker.nanodegreecapstone.common.models.Story;
import io.reactivex.Observable;


public interface StoryRepository {

    @NonNull
    Observable<List<Story>> getAllStories();

    void saveStories(Story[] stories);
}
