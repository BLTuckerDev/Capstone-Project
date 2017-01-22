package dev.bltucker.nanodegreecapstone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import dev.bltucker.nanodegreecapstone.injection.ApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.ApplicationResourcesModule;
import dev.bltucker.nanodegreecapstone.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.sync.CommentCleaningService;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;
import rx.Completable;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CapstoneApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        createApplicationComponent();
        createSyncAdapterAccount();
        createCommentCleanUpJob();
    }

    private void createCommentCleanUpJob() {
        Completable.fromAction(new Action0() {
            @Override
            public void call() {

                JobInfo.Builder jobBuilder = new JobInfo.Builder(CommentCleaningService.JOB_ID,
                        new ComponentName(CapstoneApplication.this, CommentCleaningService.class));

                final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
                jobBuilder.setPeriodic(MILLIS_IN_DAY);

                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(jobBuilder.build());
            }
        }).subscribeOn(Schedulers.computation())
                .subscribe(new Completable.CompletableSubscriber() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Error attempting to start the job scheduler service.");
                    }

                    @Override
                    public void onSubscribe(Subscription d) {   }
                });
    }

    protected void createApplicationComponent() {
        final ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationResourcesModule(new ApplicationResourcesModule(this))
                .build();

        DaggerInjector.initializeInjector(applicationComponent);
    }

    private void createSyncAdapterAccount(){
        Account newAccount = new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);
    }
}
