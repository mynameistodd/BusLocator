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
import com.sliverbit.buslocator.models.Route;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<String> checkedItems;
    private ArrayList<Route> routes;

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
        routes = args.getParcelableArrayList("routes");
        List<String> temp = new ArrayList<>();
        for (Route routeName : routes) {
            temp.add(routeName.getRouteDisplay() + " - " + routeName.getRouteName());
        }

        this.routeItems = temp;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Set<String> savedRoutes = prefs.getStringSet(getString(R.string.saved_route_index_set), new HashSet<String>());

        checkedItems = savedRoutes;

        CharSequence[] items = new CharSequence[routeItems.size()];
        final boolean[] checkeditems = new boolean[routeItems.size()];
        for (String savedItemIndex : savedRoutes) {
            checkeditems[Integer.parseInt(savedItemIndex)] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_route)
                .setMultiChoiceItems(routeItems.toArray(items), checkeditems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkeditems[which] = isChecked;
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        checkedItems.clear();
                        for (int i = 0; i < checkeditems.length; i++) {
                            if (checkeditems[i]) {
                                checkedItems.add(String.valueOf(i));
                            }
                        }

                        prefsEditor.putStringSet(getString(R.string.saved_route_index_set), checkedItems);
                        prefsEditor.putStringSet(getString(R.string.saved_route_abbr_set), getRouteAbbr(checkedItems));
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

    private String getRouteAbbr(int savedRouteIndex) {
        return (routes.size() > 0) ? routes.get(savedRouteIndex).getRouteDisplay() : "18";
    }

    private Set<String> getRouteAbbr(Set<String> savedRoutes) {
        Set<String> results = new HashSet<>();
        for (String savedRoute : savedRoutes) {
            results.add(getRouteAbbr(Integer.parseInt(savedRoute)));
        }
        return results;
    }

    public interface RouteDialogListener {
        void onDialogDismissed();
    }
}
