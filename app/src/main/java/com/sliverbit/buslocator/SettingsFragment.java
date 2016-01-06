package com.sliverbit.buslocator;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class SettingsFragment extends PreferenceFragment {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.preferences);

        Preference versionPref = findPreference(getString(R.string.pref_version_key));
        versionPref.setSummary(BuildConfig.VERSION_NAME);
    }

    @Override
    public void onStart() {
        super.onStart();
        analytics = GoogleAnalytics.getInstance(getActivity());
        tracker = analytics.newTracker(R.xml.global_tracker);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.white, null));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
