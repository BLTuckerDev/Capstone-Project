package dev.bltucker.nanodegreecapstone.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public class CombinationBackedStoryRepository implements StoryRepository {
    //TODO test this class!
    public static final int CACHE_SIZE = 2 * 1024 * 1024; // 2MiB

    private final ContentResolver contentResolver;
    private final HackerNewsApiService hackerNewsApiService;
    private final DescendingScoreStoryComparator storyComparator;

    private LruCache<Long, List<Comment>> commentLruCache;

    @Inject
    public CombinationBackedStoryRepository(ContentResolver contentResolver, HackerNewsApiService hackerNewsApiService, DescendingScoreStoryComparator storyComparator) {
        this.contentResolver = contentResolver;
        this.hackerNewsApiService = hackerNewsApiService;
        this.storyComparator = storyComparator;
        commentLruCache = new LruCache<>(CACHE_SIZE);
    }

    @Override
    public Observable<List<Story>> getAllStories() {
        return Observable.create(new Observable.OnSubscribe<List<Story>>() {
            @Override
            public void call(Subscriber<? super List<Story>> subscriber) {
                Cursor query = contentResolver.query(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES,
                        null,
                        null,
                        null,
                        null);

                List<Story> storyList = new ArrayList<>(query.getCount());

                while (query.moveToNext()) {
                    long storyId = query.getLong(query.getColumnIndex(StoryColumns._ID));
                    String storyPoster = query.getString(query.getColumnIndex(StoryColumns.POSTER_NAME));
                    long score = query.getLong(query.getColumnIndex(StoryColumns.SCORE));
                    String title = query.getString(query.getColumnIndex(StoryColumns.TITLE));
                    long unixTime = query.getLong(query.getColumnIndex(StoryColumns.UNIX_TIME));
                    String storyUrl = query.getString(query.getColumnIndex(StoryColumns.URL));
                    Long[] commentIds = getCommentIds(storyId);

                    storyList.add(new Story(storyId, storyPoster, score, unixTime, title, storyUrl, commentIds));
                }

                query.close();
                subscriber.onNext(storyList);
                subscriber.onCompleted();
            }
        });
    }

    private Long[] getCommentIds(long storyId) {
        Cursor query = contentResolver.query(SchematicContentProviderGenerator.CommentRefs.withStoryId(String.valueOf(storyId)), null, null, null, null);
        Long[] commentIds = new Long[query.getCount()];
        int index = 0;
        while (query.moveToNext()) {
            commentIds[index] = query.getLong(query.getColumnIndex(CommentRefsColumns._ID));
            index++;
        }

        query.close();
        return commentIds;
    }

    @Override
    public Observable<List<Comment>> getStoryComments(final Story story) {
        List<Comment> cachedComments = commentLruCache.get(story.getId());

        if (cachedComments != null) {
            return Observable.just(cachedComments);
        }

        Long[] commentIds = getCommentIds(story.getId());
        Observable<List<Comment>> listObservable = Observable.from(commentIds)
                .concatMap(new Func1<Long, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(Long commentId) {
                        return Observable.from(addCommentToList(commentId));
                    }
                })
                .toList();

        return listObservable;
    }

    public List<Comment> addCommentToList(long commentId){

        List<Comment> commentList = new ArrayList<>();
        Comment comment = hackerNewsApiService.getComment(commentId).toBlocking().first();

        if (comment.getAuthorName() != null && comment.getCommentText() != null) {
            commentList.add(comment);
            if(comment.getReplyIds().length > 0){
                for (int i = 0; i < comment.getReplyIds().length; i++) {
                    commentList.addAll(addCommentToList(comment.getReplyIds()[i]));
                }
            }
        }
        return commentList;
    }

    @Override
    public void saveStories(List<Story> stories) {
        commentLruCache.evictAll();
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
