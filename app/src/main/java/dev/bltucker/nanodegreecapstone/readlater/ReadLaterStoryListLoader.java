package dev.bltucker.nanodegreecapstone.readlater;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.common.models.ReadLaterStory;

public class ReadLaterStoryListLoader extends AsyncTaskLoader<List<ReadLaterStory>> {

    public static final int ID = ReadLaterStoryListLoader.class.hashCode();

    private final ReadLaterRepository readLaterRepository;

    @Inject
    public ReadLaterStoryListLoader(Context context, ReadLaterRepository readLaterRepository) {
        super(context);
        this.readLaterRepository = readLaterRepository;
        onContentChanged();
    }

    @Override
    public List<ReadLaterStory> loadInBackground() {
        return readLaterRepository.getAll().blockingFirst();
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        }
    }
}
