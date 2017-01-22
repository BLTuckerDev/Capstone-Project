package dev.bltucker.nanodegreecapstone.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.data.StoryDatabase;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import rx.Completable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CommentCleaningService extends JobService {

    public static final int JOB_ID = 1;

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
                Timber.e(e, "Error attempting to complete comment clean up");
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

}
