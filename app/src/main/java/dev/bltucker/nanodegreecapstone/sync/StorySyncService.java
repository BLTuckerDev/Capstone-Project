package dev.bltucker.nanodegreecapstone.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StorySyncService extends Service {

    private static StorySyncAdapter storySyncAdapter;

    private static final Object storySyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (storySyncAdapterLock){
            if(null == storySyncAdapter){
                storySyncAdapter = new StorySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return storySyncAdapter.getSyncAdapterBinder();
    }
}
