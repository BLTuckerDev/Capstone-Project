package dev.bltucker.nanodegreecapstone.readlater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.ReadLaterColumns;
import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import rx.Observable;
import rx.Subscriber;

public class ContentProviderBackedRepository implements ReadLaterRepository {

    private final Context context;

    @Inject public ContentProviderBackedRepository(Context context){
        this.context = context;
    }

    @Override
    public void save(ReadLaterStory readLaterStory) {
        ContentValues contentValues = ReadLaterStory.mapToContentValues(readLaterStory);
        context.getContentResolver().insert(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, contentValues);
    }

    @Override
    public void remove(ReadLaterStory readLaterStory) {
        context.getContentResolver().delete(SchematicContentProviderGenerator.ReadLaterStoryPaths.withStoryId(String.valueOf(readLaterStory.getId())), null, null);
    }

    @Override
    public Observable<List<ReadLaterStory>> getAll() {
        return Observable.create(new Observable.OnSubscribe<List<ReadLaterStory>>() {
            @Override
            public void call(Subscriber<? super List<ReadLaterStory>> subscriber) {
                Cursor query = context.getContentResolver().query(SchematicContentProviderGenerator.ReadLaterStoryPaths.ALL_READ_LATER_STORIES,
                        null,
                        null,
                        null,
                        null);

                List<ReadLaterStory> readLaterStories = new ArrayList<ReadLaterStory>(query.getCount());
                while(query.moveToNext()){
                    long storyId = query.getLong(query.getColumnIndex(ReadLaterColumns._ID));
                    String posterName = query.getString(query.getColumnIndex(ReadLaterColumns.POSTER_NAME));
                    String title = query.getString(query.getColumnIndex(ReadLaterColumns.TITLE));
                    String url = query.getString(query.getColumnIndex(ReadLaterColumns.URL));

                    readLaterStories.add(new ReadLaterStory(storyId, posterName, title, url));
                }

                query.close();
                subscriber.onNext(readLaterStories);
                subscriber.onCompleted();
            }
        });
    }
}
