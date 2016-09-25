package dev.bltucker.nanodegreecapstone.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class CommentRepository {

    private final ContentResolver contentResolver;

    @Inject
    public CommentRepository(ContentResolver contentResolver){
        this.contentResolver = contentResolver;
    }

    public void saveComment(Comment comment){
        ContentValues commentContentValues = Comment.mapToContentValues(comment);

        contentResolver.insert(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, commentContentValues);

        Timber.d("Saved comment with comment id: %d", comment.getId());
    }

    public Observable<List<Comment>> getStoryComments(final long storyId) {
        Cursor commentCursor = contentResolver.query(SchematicContentProviderGenerator.CommentPaths.withStoryId(String.valueOf(storyId)),
                null,
                null,
                null,
                null);

        if (null == commentCursor) {
            return Observable.error(new Exception(String.format(Locale.US, "Query for story with id %d comments a null cursor", storyId)));
        }

        return Observable.just(commentCursor)
                .map(new Func1<Cursor, List<Comment>>() {
                    @Override
                    public List<Comment> call(Cursor commentCursor) {
                        List<Comment> commentList = new ArrayList<>(commentCursor.getCount());

                        while (commentCursor.moveToNext()) {
                            long commentId = commentCursor.getLong(commentCursor.getColumnIndex(CommentColumns._ID));
                            String commentAuthor = commentCursor.getString(commentCursor.getColumnIndex(CommentColumns.AUTHOR_NAME));
                            String commentText = commentCursor.getString(commentCursor.getColumnIndex(CommentColumns.COMMENT_TEXT));
                            long unixTime = commentCursor.getLong(commentCursor.getColumnIndex(CommentColumns.UNIX_POST_TIME));
                            long parentId = commentCursor.getLong(commentCursor.getColumnIndex(CommentColumns.PARENT_ID));

                            commentList.add(new Comment(commentId, commentAuthor, commentText, unixTime, parentId));
                        }

                        commentCursor.close();
                        return commentList;
                    }
                });
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
}
