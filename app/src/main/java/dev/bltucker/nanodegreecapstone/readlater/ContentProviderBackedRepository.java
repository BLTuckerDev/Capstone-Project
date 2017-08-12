package dev.bltucker.nanodegreecapstone.readlater;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.daos.ReadLaterStoryDao;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import io.reactivex.Observable;

public class ContentProviderBackedRepository implements ReadLaterRepository {

    @NonNull
    private final ReadLaterStoryDao readLaterStoryDao;

    @Inject
    public ContentProviderBackedRepository(@NonNull ReadLaterStoryDao readLaterStoryDao){
        this.readLaterStoryDao = readLaterStoryDao;
    }

    @Override
    public void save(ReadLaterStory readLaterStory) {
        readLaterStoryDao.saveStory(readLaterStory);
    }

    @Override
    public void remove(ReadLaterStory readLaterStory) {
        readLaterStoryDao.deleteStory(readLaterStory);
    }

    @Override
    public Observable<List<ReadLaterStory>> getAll() {
        return readLaterStoryDao.getAllReadLaterStories().toObservable();
    }
}
