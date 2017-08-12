package dev.bltucker.nanodegreecapstone.home;


import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;

import static dev.bltucker.nanodegreecapstone.StoryProvider.AUTHORITY;

@ApplicationScope
public class ContentSyncRequester {

    @SuppressWarnings("squid:S1186")
    @Inject
    public ContentSyncRequester(){
    }

    public void requestImmediateSync(Account account){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, AUTHORITY, bundle);
    }

    public void requestPeriodicSync(Account account, int syncInterval){
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
        ContentResolver.addPeriodicSync(
                account,
                AUTHORITY,
                Bundle.EMPTY,
                syncInterval);
    }

}
