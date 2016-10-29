package com.sliverbit.buslocator;

import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.sliverbit.buslocator.models.BustimeResponse;
import com.sliverbit.buslocator.models.Pattern;
import com.sliverbit.buslocator.models.Point;
import com.sliverbit.buslocator.models.Route;
import com.sliverbit.buslocator.models.Vehicle;
import com.sliverbit.buslocator.service.BusTimeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class MainActivity extends AppCompatActivity implements
        RouteDialogFragment.RouteDialogListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String API_KEY = "sg4ttUThYrqW8xnZU43Pebs25";
    private static final String TAG_MAP_FRAGMENT = "mapFragment";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private Tracker tracker;
    private ArrayList<Route> routes;
    private SharedPreferences prefs;
    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private HashMap<String, Vehicle> busMarkerHashMap;
    private AdView adView;
    private AdRequest adRequest;
    private MapFragment mapFragment;
    private BusTimeService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mapFragment = MapFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mapFragment, TAG_MAP_FRAGMENT)
                    .commit();
        } else {
            mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(TAG_MAP_FRAGMENT);
        }

        mapFragment.getMapAsync(this);

        BusLocatorApplication application = (BusLocatorApplication) getApplication();
        tracker = application.getDefaultTracker();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = getPreferences(Context.MODE_PRIVATE);

        adView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.test_device_id))
                .build();
        adView.loadAd(adRequest);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        busMarkerHashMap = new HashMap<>();
        routes = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://rt.theride.org")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        service = retrofit.create(BusTimeService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Call<BustimeResponse> retroRoutes = service.getRoutes(API_KEY);
        retroRoutes.enqueue(new Callback<BustimeResponse>() {
            @Override
            public void onResponse(Call<BustimeResponse> call, retrofit2.Response<BustimeResponse> response) {
                BustimeResponse bustimeResponse = response.body();

                routes.clear();
                for (Route route : bustimeResponse.getRoute()) {
                    routes.add(route);
                }
            }

            @Override
            public void onFailure(Call<BustimeResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.no_route_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    map.setMyLocationEnabled(true);
                    googleApiClient.reconnect();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
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
        Set<String> savedRouteAbbrSet = prefs.getStringSet(getString(R.string.saved_route_abbr_set), new HashSet<String>() {{
            add("32");
        }});
        MenuItem item = menu.findItem(R.id.action_route);
        if (item != null) {
            item.setTitle("Routes " + TextUtils.join(",", savedRouteAbbrSet));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_route:
                if (routes != null && routes.size() > 0) {
                    Bundle args = new Bundle();
                    args.putParcelableArrayList("routes", routes);
                    DialogFragment routeDialogFragment = new RouteDialogFragment();
                    routeDialogFragment.setArguments(args);
                    routeDialogFragment.show(getFragmentManager(), "routeFragment");
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_route_data, Toast.LENGTH_SHORT).show();
                }
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
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new SettingsFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            map.setMyLocationEnabled(true);
        }

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

                Vehicle busLocation = busMarkerHashMap.get(marker.getId());

                TextView busAdherence = (TextView) busInfoWindow.findViewById(R.id.busAdherence);
                TextView busRoute = (TextView) busInfoWindow.findViewById(R.id.busRoute);
                TextView busNum = (TextView) busInfoWindow.findViewById(R.id.busNum);
                TextView busUpdated = (TextView) busInfoWindow.findViewById(R.id.busUpdated);
                TextView busDirection = (TextView) busInfoWindow.findViewById(R.id.busDirection);

                busAdherence.setText(busLocation.getDes());
                busRoute.setText("Route# " + busLocation.getRt());
                busNum.setText("Bus# " + busLocation.getVid());
                busUpdated.setText("Updated: " + busLocation.getTmstmp());
                busDirection.setText("Direction: " + busLocation.getDes());

//                if (busLocation.getAdherence() > 0) {
//                    busAdherence.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
//                } else if (busLocation.getAdherence() < 0) {
//                    busAdherence.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
//                }

                return busInfoWindow;
            }
        });

        UiSettings settings = map.getUiSettings();

        settings.setAllGesturesEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMapToolbarEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setZoomControlsEnabled(true);

        refresh();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        android.location.Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (lastLocation != null && map != null) {
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
        if (map != null) {
            map.clear();
        }

        busMarkerHashMap.clear();

        Set<String> savedRouteAbbrSet = prefs.getStringSet(getString(R.string.saved_route_abbr_set), new HashSet<String>() {{
            add("32");
        }});

        for (String savedRouteAbbr : savedRouteAbbrSet) {

            Call<BustimeResponse> retroPatterns = service.getPatterns(API_KEY, savedRouteAbbr);
            retroPatterns.enqueue(new Callback<BustimeResponse>() {
                @Override
                public void onResponse(Call<BustimeResponse> call, retrofit2.Response<BustimeResponse> response) {
                    BustimeResponse bustimeResponse = response.body();

                    PolylineOptions lineOptions = new PolylineOptions();
                    lineOptions.color(Color.BLUE);

                    for (Pattern routePattern : bustimeResponse.getPattern()) {
                        for (Point point : routePattern.getPoint()) {
                            Double lat = point.getLat();
                            Double lng = point.getLon();

                            LatLng latLngPoint = new LatLng(lat, lng);

                            lineOptions.add(latLngPoint);
                        }
                    }

                    map.addPolyline(lineOptions);
                }

                @Override
                public void onFailure(Call<BustimeResponse> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.no_route_data, Toast.LENGTH_SHORT).show();
                }
            });


            Call<BustimeResponse> retroLocations = service.getVehicles(API_KEY, savedRouteAbbr);
            retroLocations.enqueue(new Callback<BustimeResponse>() {
                @Override
                public void onResponse(Call<BustimeResponse> call, retrofit2.Response<BustimeResponse> response) {
                    BustimeResponse bustimeResponse = response.body();

                    LatLng busLatLng = null;
                    Marker busMarker = null;

                    for (Vehicle busLocation : bustimeResponse.getVehicle()) {
                        Double lat = busLocation.getLat();
                        Double lng = busLocation.getLon();

                        busLatLng = new LatLng(lat, lng);

                        busMarker = map.addMarker(new MarkerOptions()
                                .position(busLatLng)
                                .title(busLocation.getDes())
                                .snippet("Bus# " + busLocation.getVid() + " Updated: " + busLocation.getTmstmp())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_bus)));

                        busMarkerHashMap.put(busMarker.getId(), busLocation);
                    }

//                    if (busLatLng != null) {
//                        map.animateCamera(CameraUpdateFactory.newLatLng(busLatLng));
//                    }
//                    if (busMarker != null) {
//                        busMarker.showInfoWindow();
//                    }
                }

                @Override
                public void onFailure(Call<BustimeResponse> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.no_location_data, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
