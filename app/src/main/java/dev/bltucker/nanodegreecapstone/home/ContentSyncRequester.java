package dev.bltucker.nanodegreecapstone.home;


import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;

@ApplicationScope
public class ContentSyncRequester {

    @Inject
    public ContentSyncRequester(){
    }

    public void requestImmediateSync(Account account){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account,
                SchematicContentProviderGenerator.AUTHORITY, bundle);
    }

    public void requestPeriodicSync(Account account, int syncInterval){
        ContentResolver.setSyncAutomatically(account, SchematicContentProviderGenerator.AUTHORITY, true);
        ContentResolver.addPeriodicSync(
                account,
                SchematicContentProviderGenerator.AUTHORITY,
                Bundle.EMPTY,
                syncInterval);
    }

}
