package com.sliverbit.buslocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sliverbit.buslocator.models.RouteName;

import java.util.ArrayList;
import java.util.List;

/**
 * BusLocator
 * Created by todd on 5/17/15.
 */
public class RouteDialogFragment extends DialogFragment {

    private Tracker tracker;
    private RouteDialogListener listener;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private List<String> routeItems;
    private int selectedItem;

    public RouteDialogFragment() {
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (RouteDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement RouteDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        BusLocatorApplication application = (BusLocatorApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        Bundle args = getArguments();
        ArrayList<RouteName> routes = args.getParcelableArrayList("routes");
        List<String> temp = new ArrayList<>();
        for (RouteName routeName : routes) {
            temp.add(routeName.getRouteAbbr() + " - " + routeName.getName());
        }

        this.routeItems = temp;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedItem = 0;
        int savedRoute = prefs.getInt(getString(R.string.saved_route), 0);
        CharSequence[] items = new CharSequence[routeItems.size()];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_route)
                .setSingleChoiceItems(routeItems.toArray(items), savedRoute, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        selectedItem = which;
                        tracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("UX")
                                        .setAction("click")
                                        .setLabel(String.valueOf(selectedItem))
                                        .build()
                        );
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prefsEditor.putInt(getString(R.string.saved_route), selectedItem);
                        prefsEditor.commit();

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
        listener.onDialogDismissed();
    }

    public interface RouteDialogListener {
        void onDialogDismissed();
    }
}
