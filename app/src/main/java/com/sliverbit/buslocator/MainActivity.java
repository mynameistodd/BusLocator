package com.sliverbit.buslocator;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sliverbit.buslocator.models.Location;
import com.sliverbit.buslocator.models.RouteName;
import com.sliverbit.buslocator.models.StopsOnRoute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements
        RouteDialogFragment.RouteDialogListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Tracker tracker;
    private ArrayList<RouteName> routes;
    private SharedPreferences prefs;
    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private RequestQueue queue;
    private HashMap<String, Location> busMarkerHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        prefs = getPreferences(Context.MODE_PRIVATE);

        BusLocatorApplication application = (BusLocatorApplication) getApplication();
        tracker = application.getDefaultTracker();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        queue = Volley.newRequestQueue(this);
        busMarkerHashMap = new HashMap<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();

        routes = new ArrayList<>();

        String urlRouteName = "http://microapi.theride.org/routenames/";
        GsonRequest<RouteName[]> routeNameRequest = new GsonRequest<>(urlRouteName, RouteName[].class, null,
                new Response.Listener<RouteName[]>() {
                    @Override
                    public void onResponse(RouteName[] response) {
                        if (response != null) {
                            Collections.addAll(routes, response);
                            Collections.sort(routes, new Comparator<RouteName>() {
                                @Override
                                public int compare(RouteName lhs, RouteName rhs) {
                                    String aRoute = lhs.getRouteAbbr();
                                    String bRoute = rhs.getRouteAbbr();

                                    String pattern = "[^0-9]+";

                                    String aRouteClean = aRoute.replaceAll(pattern, "");
                                    String bRouteClean = bRoute.replaceAll(pattern, "");

                                    int aRouteInt = Integer.parseInt(aRouteClean);
                                    int bRouteInt = Integer.parseInt(bRouteClean);

                                    return (aRouteInt == bRouteInt) ? 0 : (aRouteInt > bRouteInt) ? 1 : -1;
                                }
                            });
                            refresh();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        queue.add(routeNameRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int savedRoute = prefs.getInt(getString(R.string.saved_route), 0);
        menu.findItem(R.id.action_route).setTitle("Route " + getRouteID(savedRoute));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_route:
                Bundle args = new Bundle();
                args.putParcelableArrayList("routes", routes);
                DialogFragment routeDialogFragment = new RouteDialogFragment();
                routeDialogFragment.setArguments(args);
                routeDialogFragment.show(getFragmentManager(), "routeFragment");
                tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("UX")
                                .setAction("click")
                                .setLabel("route picker")
                                .build()
                );
                return true;
            case R.id.action_refresh:
                tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("UX")
                                .setAction("click")
                                .setLabel("refresh")
                                .build()
                );
                refresh();
                return true;
//            case R.id.action_settings:
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMyLocationEnabled(true);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (busMarkerHashMap == null) {
                    return null;
                }

                View busInfoWindow = getLayoutInflater().inflate(R.layout.bus_info_window, null);

                Location busLocation = busMarkerHashMap.get(marker.getId());

                TextView busNum = (TextView) busInfoWindow.findViewById(R.id.busNum);
                TextView busAdherence = (TextView) busInfoWindow.findViewById(R.id.busAdherence);
                TextView busUpdated = (TextView) busInfoWindow.findViewById(R.id.busUpdated);
                TextView busDirection = (TextView) busInfoWindow.findViewById(R.id.busDirection);

                busNum.setText("Bus# " + busLocation.getBusNum());
                busAdherence.setText(busLocation.getAdherenceText());
                busUpdated.setText("Updated: " + busLocation.getTimestamp());
                busDirection.setText("Direction: " + busLocation.getRouteDirection());

                if (busLocation.getAdherence() > 0) {
                    busAdherence.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else if (busLocation.getAdherence() < 0) {
                    busAdherence.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }

                return busInfoWindow;
            }
        });

        UiSettings settings = map.getUiSettings();

        settings.setAllGesturesEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMapToolbarEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setZoomControlsEnabled(true);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        android.location.Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (lastLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 14));
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDialogDismissed() {
        invalidateOptionsMenu();
        refresh();
    }

    private void refresh() {
        map.clear();
        busMarkerHashMap.clear();
        int savedRoute = prefs.getInt(getString(R.string.saved_route), 0);

        String urlStopsRoute = "http://microapi.theride.org/StopsOnRoute/" + getRouteID(savedRoute);
        String urlLocation = "http://microapi.theride.org/Location/" + getRouteID(savedRoute);

        GsonRequest<StopsOnRoute[]> routeRequest = new GsonRequest<>(urlStopsRoute, StopsOnRoute[].class, null,
                new Response.Listener<StopsOnRoute[]>() {
                    @Override
                    public void onResponse(StopsOnRoute[] response) {
                        if (response != null) {
                            PolylineOptions lineOptions = new PolylineOptions();
                            lineOptions.color(Color.BLUE);

                            for (StopsOnRoute busLocation : response) {
                                String lat = busLocation.getLattitude();
                                String lng = busLocation.getLongitude();

                                LatLng busLatLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

                                lineOptions.add(busLatLng);
                            }

                            map.addPolyline(lineOptions);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        GsonRequest<Location[]> locationRequest = new GsonRequest<>(urlLocation, Location[].class, null,
                new Response.Listener<Location[]>() {
                    @Override
                    public void onResponse(Location[] response) {
                        if (response != null) {
                            LatLng busLatLng = null;
                            Marker busMarker = null;

                            for (Location busLocation : response) {
                                String lat = busLocation.getLat();
                                String lng = busLocation.getLongitude();

                                busLatLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

                                busMarker = map.addMarker(new MarkerOptions()
                                        .position(busLatLng)
                                        .title(busLocation.getAdherenceText())
                                        .snippet("Bus# " + busLocation.getBusNum() + " Updated: " + busLocation.getTimestamp())
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_bus)));

                                busMarkerHashMap.put(busMarker.getId(), busLocation);
                            }

                            if (busLatLng != null) {
                                map.animateCamera(CameraUpdateFactory.newLatLng(busLatLng));
                            }
                            if (busMarker != null) {
                                busMarker.showInfoWindow();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        queue.add(routeRequest);
        queue.add(locationRequest);
    }

    private String getRouteID(int savedRouteIndex) {
        return (routes.size() > 0) ? routes.get(savedRouteIndex).getRouteAbbr() : "18";
    }
}
