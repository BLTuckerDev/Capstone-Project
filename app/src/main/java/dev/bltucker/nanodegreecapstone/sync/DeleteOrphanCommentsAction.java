package dev.bltucker.nanodegreecapstone.sync;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

import dev.bltucker.nanodegreecapstone.data.DatabaseGenerator;
import dev.bltucker.nanodegreecapstone.data.StoryDatabase;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;
import rx.functions.Action0;
import timber.log.Timber;

class DeleteOrphanCommentsAction implements Action0 {

    private static final String SELECT_ROOT_ORPHAN_COMMENTS =
            "SELECT * FROM comments t1 where parentId NOT IN (SELECT _id FROM stories) AND commentDepth = 0";

    private static final String SELECT_CHILD_COMMENTS = "SELECT * from comments where parentId = ?";

    private final SQLiteDatabase writableDatabase;

    DeleteOrphanCommentsAction(StoryDatabase storyDatabase) {
        writableDatabase = storyDatabase.getWritableDatabase();
    }

    @Override
    public void call() {
        Cursor cursor = writableDatabase.rawQuery(SELECT_ROOT_ORPHAN_COMMENTS, new String[0]);
        int deletedCommentCount = 0;
        recusiveChildDelete(cursor, deletedCommentCount);
        Timber.d("Comment Clean up job removed %d comments", deletedCommentCount);
        cursor.close();
    }

    private void recusiveChildDelete(Cursor cursor, int deletedCommentCount){
        while(cursor.moveToNext()){

            long commentId = cursor.getLong(cursor.getColumnIndex(CommentColumns.COMMENT_ID));

            Cursor childCursor = writableDatabase.rawQuery(SELECT_CHILD_COMMENTS, new String[]{String.valueOf(commentId)});

            if(childCursor.getCount() > 0){
                recusiveChildDelete(childCursor, deletedCommentCount);
            }

            writableDatabase.delete(DatabaseGenerator.COMMENTS, "commentId = ?", new String[]{String.valueOf(commentId)});
            deletedCommentCount++;
        }
        cursor.close();
    }
}
