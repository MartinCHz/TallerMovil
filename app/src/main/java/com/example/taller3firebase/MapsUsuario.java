package com.example.taller3firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsUsuario extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_usuario);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng bogota = new LatLng(4.65, -74.05);
        mMap.addMarker(new MarkerOptions().position(bogota).title("Marker in Bogotá"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));

        // Zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        // Gestures
        mMap.getUiSettings().setAllGesturesEnabled(true);

        // Zoom Buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);



        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsUsuario.this, R.raw.style_user_map));


    }
}