package com.example.sportstracker;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_settings);
        SettingsFragment fragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.settingsContainer, new SettingsFragment()).commit();
    }


    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
            EditTextPreference distancePreference = findPreference(getString(R.string.distanceIntervalPref));
            EditTextPreference timePreference = findPreference(getString(R.string.timeIntervalPref));
            if (distancePreference != null) {
                distancePreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                });
            }
            if (timePreference != null) {
                timePreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                });
            }
            ListPreference preferredType = findPreference(getString(R.string.routeTypePref));
            if (preferredType != null) {
                changeIcon(preferredType, preferredType.getValue());
                preferredType.setOnPreferenceChangeListener(this);
            }
        }

        @Override
        public PreferenceManager getPreferenceManager() {
            return super.getPreferenceManager();
        }

        @Override
        public void onResume() {
            super.onResume();

        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            changeIcon(preference, newValue.toString());
            return true;
        }

        private void changeIcon(Preference preferredType, String value) {
            int type = Integer.parseInt(value);
            preferredType.setIcon(new RoutesMethods().getIcon(type));
            preferredType.getIcon().setTint(getResources().getColor(R.color.colorIcon, null));
        }

    }

}


