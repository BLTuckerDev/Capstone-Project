package dev.bltucker.nanodegreecapstone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;

import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;

public class CapstoneApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        createSyncAdapterAccount();
    }

    private void createSyncAdapterAccount(){
        Account newAccount = new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);
    }
}
