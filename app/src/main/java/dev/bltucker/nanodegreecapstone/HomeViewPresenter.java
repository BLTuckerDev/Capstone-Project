package dev.bltucker.nanodegreecapstone;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.injection.SyncIntervalSeconds;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;

public class HomeViewPresenter {

    private final int syncInterval;
    private HomeView view;

    @Inject
    public HomeViewPresenter(@SyncIntervalSeconds int syncInterval){
        this.syncInterval = syncInterval;
    }

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
              syncInterval);
    }
}
