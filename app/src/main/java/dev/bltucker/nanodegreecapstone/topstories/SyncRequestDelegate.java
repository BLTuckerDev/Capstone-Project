package dev.bltucker.nanodegreecapstone.topstories;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;

class SyncRequestDelegate {
    private final Account account;

    public SyncRequestDelegate(Account account) {
        this.account = account;
    }

    void sendSyncRequest() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, SchematicContentProviderGenerator.AUTHORITY, bundle);
    }
}