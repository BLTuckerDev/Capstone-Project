package dev.bltucker.nanodegreecapstone.location;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //inject here
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            return;
        }


        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            sendEnterNotification();
        }

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            sendDwellNotification();
        }

    }

    private void sendDwellNotification() {

    }

    private void sendEnterNotification() {

    }
}
