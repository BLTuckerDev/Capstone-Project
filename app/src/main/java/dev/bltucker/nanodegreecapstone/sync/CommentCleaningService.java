package dev.bltucker.nanodegreecapstone.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CommentCleaningService extends JobService {

    public static final int JOB_ID = 1;

    @Inject
    CommentRepository commentRepository;

    @Inject
    CommentsDao commentsDao;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerInjector.getApplicationComponent().inject(this);
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        Completable.fromAction(new DeleteOrphanCommentsAction(commentsDao))
        .subscribeOn(Schedulers.io())
        .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onComplete() {
                jobFinished(jobParameters, false);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error attempting to complete comment clean up");
                jobFinished(jobParameters, false);
            }
        });

        //returning true means the job is not done yet.
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}
