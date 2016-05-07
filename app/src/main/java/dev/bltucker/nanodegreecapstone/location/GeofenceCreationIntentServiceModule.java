package dev.bltucker.nanodegreecapstone.location;

import android.location.Geocoder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import dagger.Module;
import dagger.Provides;

@Module
public class GeofenceCreationIntentServiceModule {

    private final GeofenceCreationService service;

    public GeofenceCreationIntentServiceModule(GeofenceCreationService service){
        this.service = service;
    }


    @Provides
    public Geocoder provideGeoCoder(){
        return new Geocoder(service);
    }

    @Provides
    public GoogleApiClient provideGoogleApiClient(){
        return new GoogleApiClient.Builder(service)
                .addApi(LocationServices.API)
                .build();
    }

    @Provides GeofencingRequestProvider provideGeofencingRequestProvider(){
        return new GeofencingRequestProvider();
    }
}
