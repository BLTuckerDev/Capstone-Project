package dev.bltucker.nanodegreecapstone.readlater;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import rx.Observable;

public interface ReadLaterRepository {

    void save(ReadLaterStory readLaterStory);

    void remove(ReadLaterStory readLaterStory);

    Observable<List<ReadLaterStory>> getAll();
}
