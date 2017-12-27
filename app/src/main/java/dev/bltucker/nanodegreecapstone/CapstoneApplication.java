package dev.bltucker.nanodegreecapstone;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.common.injection.ApplicationComponent;
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationResourcesModule;
import dev.bltucker.nanodegreecapstone.common.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.logging.FirebaseDebugTree;
import dev.bltucker.nanodegreecapstone.sync.CommentCleaningService;
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesUpdateService;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CapstoneApplication extends Application {

    @Inject
    FirebaseJobDispatcher firebaseJobDispatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new FirebaseDebugTree());
        }
        createApplicationComponent();
        createSyncAdapterAccount();
        scheduleCommentCleanUpJob();
    }

    protected void createApplicationComponent() {
        final ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationResourcesModule(new ApplicationResourcesModule(this))
                .build();

        DaggerInjector.initializeInjector(applicationComponent);
        applicationComponent.inject(this);
        createTopStoriesSyncJob();
    }

    private void createTopStoriesSyncJob() {
        int periodicity = (int) TimeUnit.MINUTES.toSeconds(30);
        int toleranceWindow = (int) TimeUnit.MINUTES.toSeconds(10);
        Job jobRequest = firebaseJobDispatcher.newJobBuilder()
                .setService(TopStoriesUpdateService.class)
                .setTag(TopStoriesUpdateService.JOB_TAG)
                .setRecurring(true)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceWindow))
                .build();

        firebaseJobDispatcher.schedule(jobRequest);
    }

    private void createSyncAdapterAccount() {
//        Account newAccount = new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE);
//        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
//        accountManager.addAccountExplicitly(newAccount, null, null);
    }

    private void scheduleCommentCleanUpJob() {
        Completable.fromAction(() -> {
            JobInfo.Builder jobBuilder = new JobInfo.Builder(CommentCleaningService.JOB_ID,
                    new ComponentName(CapstoneApplication.this, CommentCleaningService.class));

            final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
            jobBuilder.setPeriodic(MILLIS_IN_DAY);

            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobBuilder.build());
        })
                .subscribeOn(Schedulers.computation())
                .subscribe(
                        () -> {
                        },
                        e -> Timber.e(e, "Error attempting to start the job scheduler service.")
                );
    }
}
