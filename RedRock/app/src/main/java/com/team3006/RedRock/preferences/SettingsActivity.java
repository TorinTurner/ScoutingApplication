/*
 * MIT License
 *
 * Copyright (c) 2019 Torin Turner (FRC 3006 RedRockRobotics)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.team3006.RedRock.preferences;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

import com.team3006.RedRock.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String EXTRA_SHOW_FRAGMENT = "show_fragment";

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof EditTextPreference) {
            preference.setSummary(stringValue);
        }

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        }
        return true;
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            PreferenceFragmentCompat fragment = null;
            if (getIntent().hasExtra(EXTRA_SHOW_FRAGMENT)) {
                try {
                    //TODO catch fragment injection
                    fragment = (PreferenceFragmentCompat) Class.forName(getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT)).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                fragment = new HeaderPreferenceFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();

            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                getSupportActionBar().setTitle("Settings");
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            recreate();
        }
    }

    public static class HeaderPreferenceFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_main);

            //Handle header clicks (not elegant)
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                getPreferenceScreen().getPreference(i).setOnPreferenceClickListener(preference -> {
                    if (preference.getFragment() == null) {
                        return false;
                    }

                    PreferenceFragmentCompat fragment;
                    try {
                        //TODO check against fragment injection
                        fragment = (PreferenceFragmentCompat) Class.forName(preference.getFragment()).newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return true;
                    }

                    HeaderPreferenceFragment.this.getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) //this mimics how some Google apps do it
                            .addToBackStack(preference.getFragment())
                            .commit();

                    return true;
                });
            }

            //Direct listeners - overrides default header listener
            Preference notificationSettings = findPreference(getResources().getString(R.string.pref_notification_settings));
            notificationSettings.setOnPreferenceClickListener(preference1 -> {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra("app_package", getActivity().getPackageName());
                    intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                }
                startActivity(intent);
                return true;
            });

            Preference appInfo = findPreference(getResources().getString(R.string.pref_app_info));
            appInfo.setOnPreferenceClickListener(preference1 -> {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivity(intent);
                return true;
            });
        }
    }

    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_general_settings);

            SettingsActivity activity = (SettingsActivity) getActivity();
            activity.getSupportActionBar().setTitle("General settings");

            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.pref_device_name)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.pref_app_theme))); //later overidden

            findPreference(getResources().getString(R.string.pref_app_theme))
                    .setOnPreferenceChangeListener((preference, newValue) -> {
                        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);

                        //TODO prompt fo LOCATION permission for Automatic and reenable

                        AppCompatDelegate.setDefaultNightMode(Integer.valueOf((String) newValue));
                        Snackbar.make(getView(), "Restart to apply theme change", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", v -> activity.recreate())
                                .show();
                        return true;
                    });
        }
    }

    public static class MatchScoutPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_match_scout);

            SettingsActivity activity = (SettingsActivity) getActivity();
            activity.getSupportActionBar().setTitle("Match scouting");
        }
    }

    public static class BluetoothServerPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_bt_server);

            SettingsActivity activity = (SettingsActivity) getActivity();
            activity.getSupportActionBar().setTitle("Bluetooth server");
        }
    }
}
