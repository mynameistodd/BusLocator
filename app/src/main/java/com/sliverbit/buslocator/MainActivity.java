package com.sliverbit.buslocator;

import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sliverbit.buslocator.models.BustimeResponse;
import com.sliverbit.buslocator.models.Pattern;
import com.sliverbit.buslocator.models.Point;
import com.sliverbit.buslocator.models.Route;
import com.sliverbit.buslocator.models.Vehicle;
import com.sliverbit.buslocator.service.BusTimeService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class MainActivity extends AppCompatActivity implements
        RouteDialogFragment.RouteDialogListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String SELECT_REFRESH = "select_refresh";

    private static final String API_KEY = "sg4ttUThYrqW8xnZU43Pebs25";
    private static final String TAG_MAP_FRAGMENT = "mapFragment";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private FirebaseAnalytics mFirebaseAnalytics;
    private ArrayList<Route> routes;
    private HashMap<String, String> routeColors;
    private SharedPreferences prefs;
    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private HashMap<String, Vehicle> busMarkerHashMap;
    private AdView adView;
    private MapFragment mapFragment;
    private BusTimeService service;
    private SimpleDateFormat simpleDateFormat;
    private Snackbar snackBar;
    private CoordinatorLayout mainCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = getPreferences(Context.MODE_PRIVATE);

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
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
        routeColors = new HashMap<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://rt.theride.org")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        service = retrofit.create(BusTimeService.class);

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.getDefault());
        mainCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);
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
                routeColors.clear();
                for (Route route : bustimeResponse.getRoute()) {
                    routes.add(route);
                    routeColors.put(route.getRouteDisplay(), route.getRouteColor());
                }
            }

            @Override
            public void onFailure(Call<BustimeResponse> call, Throwable t) {
                t.printStackTrace();
                showSnackbar(R.string.no_route_data);
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
                    showSnackbar(R.string.no_route_data);
                }
                return true;
            case R.id.action_refresh:
                Bundle bundle = new Bundle();
                bundle.putString("action", "click");
                mFirebaseAnalytics.logEvent(SELECT_REFRESH, bundle);

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

                TextView busAdherence = (TextView) busInfoWindow.findViewById(R.id.busAdherence);
                TextView busRoute = (TextView) busInfoWindow.findViewById(R.id.busRoute);
                TextView busNum = (TextView) busInfoWindow.findViewById(R.id.busNum);
                TextView busUpdated = (TextView) busInfoWindow.findViewById(R.id.busUpdated);
                TextView busDirection = (TextView) busInfoWindow.findViewById(R.id.busDirection);

                Vehicle busLocation = busMarkerHashMap.get(marker.getId());

                Date lastUpdatedDate = Calendar.getInstance().getTime();
                try {
                    lastUpdatedDate = simpleDateFormat.parse(busLocation.getTmstmp());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!busLocation.isDly()) {
                    busAdherence.setText(getString(R.string.bus_onTime));
                    busAdherence.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    busAdherence.setText(getString(R.string.bus_delayed));
                    busAdherence.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }

                busRoute.setText(getString(R.string.bus_route, String.format("%s%s", busLocation.getRt(), busLocation.getDirection())));
                busNum.setText(getString(R.string.bus_number, busLocation.getVid()));
                busUpdated.setText(getString(R.string.bus_updated, DateUtils.getRelativeTimeSpanString(lastUpdatedDate.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.SECOND_IN_MILLIS)));
                busDirection.setText(getString(R.string.bus_direction, busLocation.getDes()));

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

        if (snackBar != null) {
            snackBar.dismiss();
        }

        busMarkerHashMap.clear();

        Set<String> savedRouteAbbrSet = prefs.getStringSet(getString(R.string.saved_route_abbr_set), new HashSet<String>() {{
            add("32");
        }});

        for (final String savedRouteAbbr : savedRouteAbbrSet) {

            Call<BustimeResponse> retroPatterns = service.getPatterns(API_KEY, savedRouteAbbr);
            retroPatterns.enqueue(new Callback<BustimeResponse>() {
                @Override
                public void onResponse(Call<BustimeResponse> call, retrofit2.Response<BustimeResponse> response) {
                    BustimeResponse bustimeResponse = response.body();

                    PolylineOptions lineOptions = new PolylineOptions();
                    lineOptions.color(getRouteColor(savedRouteAbbr));

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
                    showSnackbar(R.string.no_route_data);
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
                                .icon(vectorToBitmap(R.drawable.ic_directions_bus_black_24dp, getRouteColor(savedRouteAbbr))));

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
                    showSnackbar(R.string.no_location_data);
                }
            });
        }
    }

    private int getRouteColor(String savedRouteAbbr) {
        if (routeColors.containsKey(savedRouteAbbr)) {
            String colorHash = routeColors.get(savedRouteAbbr);
            return Color.parseColor(colorHash);
        }
        return Color.BLUE;
    }

    private void showSnackbar(@StringRes int stringId) {
        if (snackBar == null || !snackBar.isShownOrQueued()) {
            snackBar = Snackbar.make(mainCoordinatorLayout, stringId, Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            });
            snackBar.show();
        }
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
