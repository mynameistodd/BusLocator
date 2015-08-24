package com.sliverbit.buslocator;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * BusLocator
 * Created by tdeland on 8/24/15.
 */
public class BusLocatorApplication extends Application {
    private Tracker tracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
            tracker.enableAutoActivityTracking(true);
            tracker.enableExceptionReporting(true);
            tracker.enableAdvertisingIdCollection(true);
        }
        return tracker;
    }
}
