package dev.bltucker.nanodegreecapstone.data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Observable;
import rx.functions.Func1;

public class ContentProviderStoryRepository implements StoryRepository {

    private final Context context;
    private final HackerNewsApiService hackerNewsApiService;

    @Inject
    public ContentProviderStoryRepository(Context context, HackerNewsApiService hackerNewsApiService){
        this.context = context;
        this.hackerNewsApiService = hackerNewsApiService;
    }

    @Override
    public List<Story> getAllStories() {
        Cursor query = context.getContentResolver().query(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES,
              null,
              null,
              null,
              null);

        List<Story> storyList = new ArrayList<>(query.getCount());

        while(query.moveToNext()){
            long storyId = query.getLong(query.getColumnIndex(StoryColumns._ID));
            String storyAuthor = query.getString(query.getColumnIndex(StoryColumns.AUTHOR_NAME));
            long score = query.getLong(query.getColumnIndex(StoryColumns.SCORE));
            String title = query.getString(query.getColumnIndex(StoryColumns.TITLE));
            long unixTime = query.getLong(query.getColumnIndex(StoryColumns.UNIX_TIME));
            String storyUrl = query.getString(query.getColumnIndex(StoryColumns.URL));
            long[] commentIds = getCommentIds(storyId);

            storyList.add(new Story(storyId, storyAuthor, score, unixTime, title,storyUrl, commentIds));
        }

        return storyList;
    }

    private long[] getCommentIds(long storyId) {
        Cursor query = context.getContentResolver().query(SchematicContentProviderGenerator.CommentRefs.withStoryId(String.valueOf(storyId)), null, null, null, null);

        long[] commentIds = new long[query.getCount()];
        int index = 0;
        while(query.moveToNext()){
            commentIds[index] = query.getLong(query.getColumnIndex(CommentRefsColumns._ID));
            index++;
        }

        return commentIds;
    }

    @Override
    public List<Comment> getStoryComments(Story story) {
        long[] commentIds = getCommentIds(story.getId());

        return Observable.just(commentIds)
              .concatMap(new Func1<long[], Observable<List<Comment>>>() {
                  @Override
                  public Observable<List<Comment>> call(long[] commentIds) {
                      List<Comment> commentObservables = new ArrayList<>(commentIds.length);

                      for (int i = 0; i < commentIds.length; i++) {
                          commentObservables.add(hackerNewsApiService.getComment(commentIds[i]).toBlocking().first());
                      }

                      return Observable.just(commentObservables);
                  }
              }).toBlocking().first();
    }
}
