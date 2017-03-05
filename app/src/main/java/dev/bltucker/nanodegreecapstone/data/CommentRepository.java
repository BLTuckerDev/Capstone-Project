package dev.bltucker.nanodegreecapstone.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@ApplicationScope
public class CommentRepository {

    private final ContentResolver contentResolver;

    @Inject
    public CommentRepository(ContentResolver contentResolver){
        this.contentResolver = contentResolver;
    }

    public Observable<List<Comment>> getStoryComments(final long storyId) {
        return getStoryCommentsByStoryId(storyId);
    }

    Long[] getCommentIds(long storyId) {
        Cursor query = contentResolver.query(SchematicContentProviderGenerator.CommentRefs.withStoryId(String.valueOf(storyId)), null, null, null, null);

        if (null == query) {
            return new Long[0];
        }

        Long[] commentIds = new Long[query.getCount()];
        int index = 0;
        while (query.moveToNext()) {
            commentIds[index] = query.getLong(query.getColumnIndex(CommentRefsColumns._ID));
            index++;
        }

        query.close();
        return commentIds;
    }

    public void saveComments(List<Comment> comments) {
        Timber.d("Saving %d comments", comments.size());
        ContentValues[] contentValues = new ContentValues[comments.size()];

        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            contentValues[i] = Comment.mapToContentValues(comment);
        }

        contentResolver.bulkInsert(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, contentValues);
    }

    public void saveComment(Comment comment){
        Timber.d("Saving single comment");
        contentResolver.insert(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, Comment.mapToContentValues(comment));
    }

    private Observable<List<Comment>> getStoryCommentsByStoryId(final long storyId){
        Timber.d("Getting comments for story with id %d", storyId);
        final Cursor commentCursor = contentResolver.query(SchematicContentProviderGenerator.CommentPaths.withStoryId(String.valueOf(storyId)),
            null,
            null,
            null,
            null);

        if(null == commentCursor){
            return Observable.error(new Exception(String.format(Locale.US, "Query for comments for story id: %d returned a null cursor", storyId)));
        }

        return Observable.just(commentCursor)
            .map(new Func1<Cursor, List<Comment>>() {
                @Override
                public List<Comment> call(Cursor cursor) {
                    List<Comment> commentList = new ArrayList<>(commentCursor.getCount());

                    while (commentCursor.moveToNext()) {
                        long storyId = commentCursor.getLong(commentCursor.getColumnIndex(CommentColumns.STORY_ID));
                        long commentId = commentCursor.getLong(commentCursor.getColumnIndex(CommentColumns.COMMENT_ID));
                        String commentAuthor = commentCursor.getString(commentCursor.getColumnIndex(CommentColumns.AUTHOR_NAME));
                        String commentText = commentCursor.getString(commentCursor.getColumnIndex(CommentColumns.COMMENT_TEXT));
                        long unixTime = commentCursor.getLong(commentCursor.getColumnIndex(CommentColumns.UNIX_POST_TIME));
                        long parentId = commentCursor.getLong(commentCursor.getColumnIndex(CommentColumns.PARENT_ID));
                        int commentDepth = commentCursor.getInt(commentCursor.getColumnIndex(CommentColumns.COMMENT_DEPTH));

                        commentList.add(new Comment(storyId, commentId, commentAuthor, commentText, unixTime, parentId, commentDepth));
                    }

                    commentCursor.close();
                    return commentList;
                }
            });

    }
}
