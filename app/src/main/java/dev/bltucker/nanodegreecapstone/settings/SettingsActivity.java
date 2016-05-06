package dev.bltucker.nanodegreecapstone.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.Geofence;

import java.io.IOException;
import java.util.List;

import dev.bltucker.nanodegreecapstone.R;
import timber.log.Timber;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static void launch(FragmentActivity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.location_preference))){
            String preference = sharedPreferences.getString(key, "");
            Timber.d("PREFERENCE IS NOW: %s", preference);
//            Geocoder geocoder = new Geocoder(this);
//            try {
//                List<Address> fromLocationName = geocoder.getFromLocationName(preference, 1);
//                Address address = fromLocationName.get(0);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
