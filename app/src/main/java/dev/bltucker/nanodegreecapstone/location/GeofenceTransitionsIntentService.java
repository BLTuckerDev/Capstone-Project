package dev.bltucker.nanodegreecapstone.location;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.home.MainActivity;

public class GeofenceTransitionsIntentService extends IntentService {

    private static final int READ_LATER_NOTIFICATION = 1;

    @Inject
    NotificationManager notificationManager;

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            sendReminderNotification();
        }
    }

    private void sendReminderNotification() {
        Notification.Builder notificationBuilder = new Notification.Builder(this);

        notificationBuilder.setSmallIcon(R.drawable.ic_book_black_24dp);
        String notificationTitle = getString(R.string.dont_forget_to_read);
        notificationBuilder.setContentTitle(notificationTitle);
        notificationBuilder.setContentText(getString(R.string.read_now));

        //TODO launch the yet to be created "saved for later" activity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingNotificationIntent);

        notificationManager.notify(READ_LATER_NOTIFICATION, notificationBuilder.build());
    }
}
