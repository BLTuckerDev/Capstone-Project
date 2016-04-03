package dev.bltucker.nanodegreecapstone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;

import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;
import timber.log.Timber;

public class CapstoneApplication extends Application {

    private static CapstoneApplication application;

    public static CapstoneApplication getApplication(){
        return application;
    }

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        application = this;
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationResourcesModule(new ApplicationResourcesModule(this))
                .build();
        createSyncAdapterAccount();
    }

    private void createSyncAdapterAccount(){
        Account newAccount = new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);
    }

    public ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }
}
