package dev.bltucker.nanodegreecapstone.readlater;

import java.util.List;

import dev.bltucker.nanodegreecapstone.common.models.ReadLaterStory;
import io.reactivex.Observable;

public interface ReadLaterRepository {

    void save(ReadLaterStory readLaterStory);

    void remove(ReadLaterStory readLaterStory);

    Observable<List<ReadLaterStory>> getAll();
}
