package com.sliverbit.buslocator;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements
        RouteDialogFragment.NoticeDialogListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private SharedPreferences mPrefs;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = getPreferences(Context.MODE_PRIVATE);

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
                return true;
            case R.id.action_refresh:
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
        int savedRoute = mPrefs.getInt(getString(R.string.saved_route), 0);
        int routeID = getRouteID(savedRoute);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://mobile.theride.org/models/GetBusLocation.aspx?routeID=" + routeID;

        GsonRequest<BusLocation[]> busLocationGsonRequest = new GsonRequest<BusLocation[]>(url, BusLocation[].class, null,
                new Response.Listener<BusLocation[]>() {
                    @Override
                    public void onResponse(BusLocation[] response) {
                        mMap.clear();

                        if (response != null) {
                            for (BusLocation busLocation : response) {
                                String lat = busLocation.getLattitude();
                                String lng = busLocation.getLongitude();


                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(lat), Double.valueOf(lng)))
                                        .title(busLocation.getAdherence())
                                        .snippet("Bus# " + busLocation.getBus() + " Updated: " + busLocation.getTimestamp())
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_bus)));
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

        queue.add(busLocationGsonRequest);
    }

    private int getRouteID(int savedRoute) {
        switch (savedRoute) {
            case 0:
                return 1;
            case 1:
                return 11;
            case 2:
                return 14;
            case 3:
                return 22;
            case 4:
                return 25;
            case 5:
                return 29;
            case 6:
                return 32;
            case 7:
                return 38;
            case 8:
                return 23;
            case 9:
                return 2;
            case 10:
                return 3;
            case 11:
                return 5;
            case 12:
                return 30;
            case 13:
                return 6;
            case 14:
                return 7;
            case 15:
                return 8;
            case 16:
                return 9;
            case 17:
                return 4;
            case 18:
                return 13;
            case 19:
                return 20;
            case 20:
                return 24;
            case 21:
                return 31;
            case 22:
                return 33;
            case 23:
                return 34;
            case 24:
                return 36;
            case 25:
                return 37;
            case 26:
                return 12;
            case 27:
                return 10;
        }
        return 0;
    }
}
