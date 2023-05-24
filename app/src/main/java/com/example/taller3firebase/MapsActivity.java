package com.example.taller3firebase;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.taller3firebase.location.LocationData;
import com.example.taller3firebase.location.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.taller3firebase.databinding.ActivityMapsBinding;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);


        // Verificar y solicitar los permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos no están otorgados, solicitarlos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        // Obtener la ubicación actual
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                // Obtener la latitud y longitud actual
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Crear objeto LatLng para la ubicación actual
                LatLng currentLatLng = new LatLng(latitude, longitude);

                // Mover la cámara al marcador de la ubicación actual
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                // Agregar marcador en la ubicación actual
                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Mi Ubicación"));

                // Obtener la lista de ubicaciones desde el archivo JSON
                List<LocationData> locations = LocationUtils.parseLocationsFromJson(MapsActivity.this);

                // Agregar marcadores para cada ubicación en el archivo JSON
                for (LocationData locationData : locations) {

                    LatLng locationLatLng = new LatLng(locationData.getLatitude(), locationData.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(locationLatLng).title(locationData.getName()));

                    //Log.d(TAG, "Added marker for location: " + locationData.getName() + ", Latitude: " + locationData.getLatitude() + ", Longitude: " + locationData.getLongitude());

                }
            }
        });
    }


}