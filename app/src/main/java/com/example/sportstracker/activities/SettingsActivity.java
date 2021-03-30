package com.example.sportstracker.activities;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sportstracker.R;
import com.example.sportstracker.RoutesMethods;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_settings);
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
            EditTextPreference horizontal = findPreference(getString(R.string.horizontalPref));
            EditTextPreference vertical = findPreference(getString(R.string.verticalPref));
            if (horizontal != null) {
                horizontal.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                });
            }
            if (vertical != null) {
                vertical.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
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
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            changeIcon(preference, newValue.toString());
            return true;
        }

        private void changeIcon(Preference preferredType, String value) {
            int type = Integer.parseInt(value);
            preferredType.setIcon(RoutesMethods.getIcon(type));
            preferredType.getIcon().setTint(getResources().getColor(R.color.colorIcon, null));
        }

    }

}
