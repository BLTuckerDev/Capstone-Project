package dev.bltucker.nanodegreecapstone.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import dev.bltucker.nanodegreecapstone.R;

public class SettingsFragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }
}
