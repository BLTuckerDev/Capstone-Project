package dev.bltucker.nanodegreecapstone.home;

import android.accounts.Account;
import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.injection.SyncIntervalSeconds;

@ApplicationScope
public class HomeViewPresenter {

    @VisibleForTesting
    HomeView view;

    @VisibleForTesting
    final Account storySyncAccount;

    @VisibleForTesting
    ContentSyncRequester contentSyncRequester;

    @VisibleForTesting
    final int syncInterval;

    @Inject
    public HomeViewPresenter(@SyncIntervalSeconds int syncInterval, Account providedAccount, ContentSyncRequester contentSyncRequester){
        this.syncInterval = syncInterval;
        this.storySyncAccount = providedAccount;
        this.contentSyncRequester = contentSyncRequester;
    }

    public void onViewCreated(HomeView createdView){
        view = createdView;
        contentSyncRequester.requestImmediateSync(storySyncAccount);
        contentSyncRequester.requestPeriodicSync(storySyncAccount, syncInterval);
    }

    public void onViewRestored(HomeView restoredView){
        view = restoredView;
    }

    public void onViewDestroyed(){
        view = null;
    }

    public void onShowReadLaterMenuClick() {
        if(view != null){
            view.showReadLaterListView();
        }
    }
}
