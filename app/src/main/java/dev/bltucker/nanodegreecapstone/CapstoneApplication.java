package dev.bltucker.nanodegreecapstone;

import android.app.Application;

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
import dev.bltucker.nanodegreecapstone.common.logging.FirebaseDebugTree;
import dev.bltucker.nanodegreecapstone.common.sync.OrphanCommentDeleteJob;
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesUpdateService;
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

    private void scheduleCommentCleanUpJob() {
        int periodicity = (int) TimeUnit.HOURS.toSeconds(12);
        int toleranceWindow = (int) TimeUnit.HOURS.toSeconds(6);

        Job jobRequest = firebaseJobDispatcher.newJobBuilder()
                .setService(OrphanCommentDeleteJob.class)
                .setRecurring(true)
                .setTag(OrphanCommentDeleteJob.JOB_TAG)
                .setConstraints(Constraint.DEVICE_IDLE)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(periodicity, periodicity+toleranceWindow))
                .build();

        firebaseJobDispatcher.schedule(jobRequest);
    }
}
