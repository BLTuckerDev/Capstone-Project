package dev.bltucker.nanodegreecapstone.location;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
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

            Address address = fromLocationName.get(0);
            Timber.d("Longitude: %f", address.getLongitude());
            Timber.d("Latitude: %f", address.getLatitude());

            if(!googleApiClient.isConnected()){
                ConnectionResult connectionResult = googleApiClient.blockingConnect(10, TimeUnit.SECONDS);
                if(!connectionResult.isSuccess()){
                    return;
                }
            }
            //noinspection MissingPermission
            LocationServices.GeofencingApi.addGeofences(googleApiClient,
                    geofencingRequestProvider.getRequest(address),
                    getPendingIntent());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
