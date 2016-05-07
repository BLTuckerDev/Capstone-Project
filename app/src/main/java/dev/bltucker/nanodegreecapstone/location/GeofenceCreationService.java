package dev.bltucker.nanodegreecapstone.location;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import timber.log.Timber;


public class GeofenceCreationService extends IntentService {
    private static final String ACTION_CREATE_GEO_FENCE = "dev.bltucker.nanodegreecapstone.location.action.CREATE_GEO_FENCE";
    private static final String LOCATION_PARAMETER = "dev.bltucker.nanodegreecapstone.location.extra.LOCATION_STRING";

    @Inject
    Geocoder geocoder;

    @Inject
    GoogleApiClient googleApiClient;

    @Inject
    GeofencingRequestProvider geofencingRequestProvider;

    public GeofenceCreationService() {
        super(GeofenceCreationService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CapstoneApplication.getApplication()
                .getApplicationComponent()
                .geofenceCreationIntentServiceComponent(new GeofenceCreationIntentServiceModule(this))
                .inject(this);
    }

    @Override
    public void onDestroy() {
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }

    public static void createGeofence(Context context, String locationString) {
        Intent intent = new Intent(context, GeofenceCreationService.class);
        intent.setAction(ACTION_CREATE_GEO_FENCE);
        intent.putExtra(LOCATION_PARAMETER, locationString);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("onHandleIntent");
        if(null == intent){
            return;
        }

        if(!intent.getAction().equals(ACTION_CREATE_GEO_FENCE) || !intent.getExtras().containsKey(LOCATION_PARAMETER)){
            return;
        }

        try {
            String locationString = intent.getStringExtra(LOCATION_PARAMETER);
            List<Address> fromLocationName = geocoder.getFromLocationName(locationString, 1);

            if(fromLocationName.isEmpty()){
                return;
            }

            Address address = fromLocationName.get(0);
            Timber.d("Longitude: %f", address.getLongitude());
            Timber.d("Latitude: %f", address.getLatitude());

            if(!googleApiClient.isConnected()){
                ConnectionResult connectionResult = googleApiClient.blockingConnect(10, TimeUnit.SECONDS);
                if(!connectionResult.isSuccess()){
                    return;
                }
            }
            //We do not ever fire an intent to this service unless we have permission.
            //noinspection MissingPermission
            LocationServices.GeofencingApi.addGeofences(googleApiClient,
                    geofencingRequestProvider.getRequest(address),
                    getPendingIntent());

        } catch (IOException e) {
            Timber.e(e, "Exception while attempting to create a geofence.");
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
