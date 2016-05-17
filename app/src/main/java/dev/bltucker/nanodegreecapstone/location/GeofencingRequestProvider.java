package dev.bltucker.nanodegreecapstone.location;

import android.location.Address;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

public class GeofencingRequestProvider {

    private static final String GEOFENCE_REQUEST_ID = "home";
    private static final int GEOFENCE_RADIUS_IN_METERS = 100;
    private static final int LOITER_DELAY_IN_MILLISECONDS = 300000;
    private static final int RESPONSIVENESS_DELAY = 300000;

    public GeofencingRequest getRequest(Address address){
        Geofence homeGeoFence = new Geofence.Builder()
                .setRequestId(GEOFENCE_REQUEST_ID)
                .setCircularRegion(address.getLatitude(),
                        address.getLongitude(),
                        GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(LOITER_DELAY_IN_MILLISECONDS)
                .setNotificationResponsiveness(RESPONSIVENESS_DELAY)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.addGeofence(homeGeoFence);
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        return builder.build();
    }
}
