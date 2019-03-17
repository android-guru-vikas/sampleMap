package com.dev.mapdemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import com.dev.mapdemo.R;
import com.dev.mapdemo.interfaces.LatLngInterpolator;
import com.dev.mapdemo.location.GetLocationDataService;
import com.dev.mapdemo.location.MarkerAnimation;
import com.dev.mapdemo.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker currentLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.KEY_NEW_LOCATION));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Intent startIntent = new Intent(MapsActivity.this, GetLocationDataService.class);
        startService(startIntent);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double lat = intent.getDoubleExtra(Constants.KEY_NEW_LAT, 0.0);
            double lng = intent.getDoubleExtra(Constants.KEY_NEW_LNG, 0.0);
            LatLng locLatLng = new LatLng(lat, lng);
            adjustMarker(locLatLng);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        adjustMarker(new LatLng(0.0, 0.0));
    }

    private void adjustMarker(LatLng latLng) {
        if (currentLocationMarker == null) {
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.delivery_location_)));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        MarkerAnimation.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMessageReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
}
