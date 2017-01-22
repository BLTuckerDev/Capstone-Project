package dev.bltucker.nanodegreecapstone.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.data.DatabaseGenerator;
import dev.bltucker.nanodegreecapstone.data.StoryDatabase;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;
import rx.Completable;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class CommentCleaningService extends JobService {

    @Inject
    CommentRepository commentRepository;

    @Inject
    StoryDatabase storyDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerInjector.getApplicationComponent().inject(this);
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        Completable.fromAction(new DeleteOrphanCommentsAction(storyDatabase))
        .subscribeOn(Schedulers.io())
        .subscribe(new Completable.CompletableSubscriber() {
            @Override
            public void onCompleted() {
                jobFinished(jobParameters, false);
            }

            @Override
            public void onError(Throwable e) {
                jobFinished(jobParameters, false);
            }

            @Override
            public void onSubscribe(Subscription d) {  }
        });


        //returning true means the job is not done yet.
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static class DeleteOrphanCommentsAction implements Action0 {

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
            recusiveChildDelete(cursor);
        }

        private void recusiveChildDelete(Cursor cursor){
            while(cursor.moveToNext()){

                long commentId = cursor.getLong(cursor.getColumnIndex(CommentColumns.COMMENT_ID));

                Cursor childCursor = writableDatabase.rawQuery(SELECT_CHILD_COMMENTS, new String[]{String.valueOf(commentId)});

                if(childCursor.getCount() > 0){
                    recusiveChildDelete(childCursor);
                }

                writableDatabase.delete(DatabaseGenerator.COMMENTS, "commentId = ?", new String[]{String.valueOf(commentId)});
            }

        }


    }
}
