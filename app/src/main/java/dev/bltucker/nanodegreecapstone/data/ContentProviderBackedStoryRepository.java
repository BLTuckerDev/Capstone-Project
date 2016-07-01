package dev.bltucker.nanodegreecapstone.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class ContentProviderBackedStoryRepository implements StoryRepository {

    @VisibleForTesting
    final ContentResolver contentResolver;

    @VisibleForTesting
    final CommentRepository commentRepository;

    @Inject
    public ContentProviderBackedStoryRepository(ContentResolver contentResolver, CommentRepository commentRepository) {
        this.contentResolver = contentResolver;
        this.commentRepository = commentRepository;
    }

    @Override
    public Observable<List<Story>> getAllStories() {

        Cursor queryCursor = contentResolver.query(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES,
                null,
                null,
                null,
                null);

        if (null == queryCursor) {
            return Observable.error(new Exception("Query for all stories returned a null cursor"));
        }

        return Observable.just(queryCursor)
                .map(new Func1<Cursor, List<Story>>() {
                    @Override
                    public List<Story> call(Cursor cursor) {
                        List<Story> storyList = new ArrayList<>(cursor.getCount());

                        while (cursor.moveToNext()) {
                            long storyId = cursor.getLong(cursor.getColumnIndex(StoryColumns._ID));
                            String storyPoster = cursor.getString(cursor.getColumnIndex(StoryColumns.POSTER_NAME));
                            long score = cursor.getLong(cursor.getColumnIndex(StoryColumns.SCORE));
                            String title = cursor.getString(cursor.getColumnIndex(StoryColumns.TITLE));
                            long unixTime = cursor.getLong(cursor.getColumnIndex(StoryColumns.UNIX_TIME));
                            String storyUrl = cursor.getString(cursor.getColumnIndex(StoryColumns.URL));
                            Long[] commentIds = commentRepository.getCommentIds(storyId);

                            storyList.add(new Story(storyId, storyPoster, score, unixTime, title, storyUrl, commentIds));
                        }

                        cursor.close();
                        return storyList;
                    }
                });
    }

    @Override
    public void saveStories(List<Story> stories) {
        commentRepository.clearInMemoryCache();
        int deletedStories = contentResolver.delete(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, null, null);
        int deletedComments = contentResolver.delete(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS, null, null);

        Timber.d("Deleted %d stories", deletedStories);
        Timber.d("Deleted %d comments", deletedComments);

        List<ContentValues> storyContentValues = new ArrayList<>(stories.size());
        List<ContentValues> commentRefsContentValuesList = new ArrayList<>();

        for (int i = 0; i < stories.size(); i++) {
            Story story = stories.get(i);
            storyContentValues.add(Story.mapToContentValues(story));
            commentRefsContentValuesList.addAll(getCommentRefList(story));
        }

        ContentValues[] contentValues = new ContentValues[storyContentValues.size()];
        int insertedStoryCount = contentResolver.bulkInsert(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, storyContentValues.toArray(contentValues));

        Timber.d("Inserted %d stories", insertedStoryCount);

        ContentValues[] commentRefsContentValuesArray = new ContentValues[commentRefsContentValuesList.size()];
        int commentRefInsertCount = contentResolver.bulkInsert(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS, commentRefsContentValuesList.toArray(commentRefsContentValuesArray));

        Timber.d("Inserted %d comment references", commentRefInsertCount);
    }

    private List<ContentValues> getCommentRefList(Story story) {
        Long[] commentIds = story.getCommentIds();
        List<ContentValues> contentValues = new ArrayList<>(commentIds.length);

        for (int i = 0; i < commentIds.length; i++) {
            ContentValues aContentValue = new ContentValues();
            aContentValue.put(CommentRefsColumns._ID, commentIds[i]);
            aContentValue.put(CommentRefsColumns.STORY_ID, story.getId());
            aContentValue.put(CommentRefsColumns.READ_RANK, i);
            contentValues.add(aContentValue);
        }

        return contentValues;
    }
}
