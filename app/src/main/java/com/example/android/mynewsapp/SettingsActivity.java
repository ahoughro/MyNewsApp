package com.example.android.mynewsapp;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference page = findPreference(getString(R.string.settings_page_key));
            bindPreferenceSummaryToValue(page);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);

            if(preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);

                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                } else {
                    preference.setSummary(stringValue);
                }
            }
            return true;
        }

        /**
         * This method takes in a Preference as its parameter, and we use setOnPreferenceChangeListener
         * to set the current EarthquakePreferenceFragment instance to listen for changes to the preference we pass in
         *
         */
        private void bindPreferenceSummaryToValue(Preference preference) {

            preference.setOnPreferenceChangeListener(this);

            /**
             * We also read the current value of the preference stored in the SharedPreferences on
             * the device, and display that in the preference summary (so that the user can see the
             * current value of the preference):
             */
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

    }
}
