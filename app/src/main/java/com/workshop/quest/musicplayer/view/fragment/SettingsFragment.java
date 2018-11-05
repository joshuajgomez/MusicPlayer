package com.workshop.quest.musicplayer.view.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.generic.Constants;
import com.workshop.quest.musicplayer.generic.ResUtil;
import com.workshop.quest.musicplayer.generic.log.Loggy;

public class SettingsFragment extends PreferenceFragment {

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Loggy.log(Log.INFO, preference.getKey() + " set to " + stringValue);
            if (preference instanceof SwitchPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                // Set the summary to reflect the new value.
            }
            return true;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        bindPreferenceSummaryToValue(findPreference(Constants.PREF_DARK_THEME));
        Preference preference = findPreference("version_name");
        preference.setSummary(ResUtil.getAppVersionName());
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof SwitchPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true));
        }
    }

}
