package com.sliverbit.buslocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * BusLocator
 * Created by todd on 5/17/15.
 */
public class RouteDialogFragment extends DialogFragment {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    RouteDialogListener mListener;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mPrefsEditor;
    private int mSelectedItem;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RouteDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement RouteDialogListener");
        }
        mPrefs = activity.getPreferences(Context.MODE_PRIVATE);
        mPrefsEditor = mPrefs.edit();

        analytics = GoogleAnalytics.getInstance(getActivity());
        tracker = analytics.newTracker(R.xml.global_tracker);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItem = 0;
        int savedRoute = mPrefs.getInt(getString(R.string.saved_route), 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_route)
                .setSingleChoiceItems(R.array.routes, savedRoute, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mSelectedItem = which;
                        tracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("UX")
                                        .setAction("click")
                                        .setLabel(String.valueOf(mSelectedItem))
                                        .build()
                        );
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPrefsEditor.putInt(getString(R.string.saved_route), mSelectedItem);
                        mPrefsEditor.commit();

                        mListener.onDialogDismissed();
                        tracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("UX")
                                        .setAction("click")
                                        .setLabel(getString(android.R.string.ok))
                                        .build()
                        );
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogDismissed();
                        tracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("UX")
                                        .setAction("click")
                                        .setLabel(getString(android.R.string.cancel))
                                        .build()
                        );
                    }
                });

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface RouteDialogListener {
        void onDialogDismissed();
    }
}
