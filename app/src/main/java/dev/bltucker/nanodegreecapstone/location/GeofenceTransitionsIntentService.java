package dev.bltucker.nanodegreecapstone.location;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.StoryProvider;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterListActivity;
import timber.log.Timber;

public class GeofenceTransitionsIntentService extends IntentService {

    private static final int READ_LATER_NOTIFICATION = 1;

    @Inject
    NotificationManager notificationManager;

    @Inject
    ContentResolver contentResolver;

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerInjector.getApplicationComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("handling a geo fence change intent");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Timber.d("geo fence event has error, will fail to handle transition");
            Timber.d("geo fence error code: %d", geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            Timber.d("geofence event transition has occurred");
            if(getReadLaterStoryCount() > 0){
                Timber.d("geofence event transition will result in a notification");
                sendReminderNotification();
            } else {
                Timber.d("There were no stories saved for later so no notification will be sent");
            }
        } else {
            Timber.d("geofence event will not result in a transition");
            Timber.d("geofence transition integer: %d", geofenceTransition);
        }
    }

    private int getReadLaterStoryCount(){
        try (Cursor readLaterStoryCursor = contentResolver.query(StoryProvider.READ_LATER_URI,
            null,
            null,
            null,
            null)) {
            return readLaterStoryCursor != null ? readLaterStoryCursor.getCount() : 0;
        }
    }

    private void sendReminderNotification() {
        Notification.Builder notificationBuilder = new Notification.Builder(this);

        notificationBuilder.setSmallIcon(R.drawable.ic_book_black_24dp);
        String notificationTitle = getString(R.string.dont_forget_to_read);
        notificationBuilder.setContentTitle(notificationTitle);
        notificationBuilder.setContentText(getString(R.string.read_now));

        Intent notificationIntent = new Intent(this, ReadLaterListActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ReadLaterListActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingNotificationIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentIntent(pendingNotificationIntent);

        notificationManager.notify(READ_LATER_NOTIFICATION, notificationBuilder.build());
    }
}
