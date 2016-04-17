package dev.bltucker.nanodegreecapstone.topstories;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.SyncCompletedEvent;
import timber.log.Timber;

public class SyncCompletedBroadCastReceiver extends BroadcastReceiver {

    @Inject
    EventBus eventBus;

    public SyncCompletedBroadCastReceiver() {
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive sync complete intent");
        eventBus.publish(new SyncCompletedEvent());
    }
}
