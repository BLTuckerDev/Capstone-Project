package dev.bltucker.nanodegreecapstone;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;

public class HomeViewPresenter {

    private static final long SYNC_INTERVAL_SECONDS = 1800;

    private HomeView view;

    public void onViewCreated(HomeView createdView){
        view = createdView;
        requestImmediateSync();
        setupPeriodicSync();
        view.showStoryList();
    }

    public void onViewRestored(HomeView restoredView){
        view = restoredView;
    }

    public void onViewDestroyed(HomeView destroyedView){
        view = null;
    }


    private void requestImmediateSync(){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE),
              SchematicContentProviderGenerator.AUTHORITY, bundle);
    }

    private void setupPeriodicSync(){
        ContentResolver.addPeriodicSync(
              new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE),
              SchematicContentProviderGenerator.AUTHORITY,
              Bundle.EMPTY,
              SYNC_INTERVAL_SECONDS);
    }
}
