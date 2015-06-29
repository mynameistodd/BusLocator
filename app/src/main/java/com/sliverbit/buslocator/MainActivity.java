package com.sliverbit.buslocator;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
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
import com.sliverbit.buslocator.models.StopsOnRoute;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements
        RouteDialogFragment.RouteDialogListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    private SharedPreferences mPrefs;
    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        mPrefs = getPreferences(Context.MODE_PRIVATE);

        analytics = GoogleAnalytics.getInstance(this);

        tracker = analytics.newTracker(R.xml.global_tracker);
        tracker.enableAdvertisingIdCollection(true);

        //Do map things
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_route:
                DialogFragment routeDialogFragment = new RouteDialogFragment();
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
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        UiSettings settings = mMap.getUiSettings();

        settings.setAllGesturesEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMapToolbarEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setZoomControlsEnabled(true);

        refresh();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        refresh();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //do nothing?
    }

    private void refresh() {
        mMap.clear();
        int savedRoute = mPrefs.getInt(getString(R.string.saved_route), 0);

        RequestQueue queue = Volley.newRequestQueue(this);
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

                            mMap.addPolyline(lineOptions);
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

                                busMarker = mMap.addMarker(new MarkerOptions()
                                        .position(busLatLng)
                                        .title(busLocation.getAdherence())
                                        .snippet("Bus# " + busLocation.getBusNum() + " Updated: " + busLocation.getTimestamp())
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_bus)));

                            }

                            if (busLatLng != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(busLatLng));
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

    private String getRouteID(int savedRoute) {
        switch (savedRoute) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            case 4:
                return "5";
            case 5:
                return "6";
            case 6:
                return "7";
            case 7:
                return "8";
            case 8:
                return "9";
            case 9:
                return "10";
            case 10:
                return "11";
            case 11:
                return "13";
            case 12:
                return "14";
            case 13:
                return "15";
            case 14:
                return "16";
            case 15:
                return "17";
            case 16:
                return "18";
            case 17:
                return "20";
            case 18:
                return "22";
            case 19:
                return "36";
            case 20:
                return "46";
            case 21:
                return "609";
            case 22:
                return "710";
            case 23:
                return "711";
            case 24:
                return "12A";
            case 25:
                return "12B";
            case 26:
                return "2C";
            case 27:
                return "1U";
        }
        return "0";
    }
}
