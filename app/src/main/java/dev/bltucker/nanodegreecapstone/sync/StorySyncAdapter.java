package dev.bltucker.nanodegreecapstone.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public final class StorySyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT = "storySyncAccount";

    public static final String ACCOUNT_TYPE = "bltucker.dev";

    private final ContentResolver contentResolver;

    public StorySyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
    }

    public StorySyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d("SYNC_ADAPTER", "Syncing.....");

    }
}
