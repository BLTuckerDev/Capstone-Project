package dev.bltucker.nanodegreecapstone.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

    StorySyncAdapterAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new StorySyncAdapterAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
