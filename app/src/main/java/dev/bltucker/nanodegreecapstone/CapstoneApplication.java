package dev.bltucker.nanodegreecapstone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;

import dev.bltucker.nanodegreecapstone.injection.ApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.ApplicationResourcesModule;
import dev.bltucker.nanodegreecapstone.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;
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
