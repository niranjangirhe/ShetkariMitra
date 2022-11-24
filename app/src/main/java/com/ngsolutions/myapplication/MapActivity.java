package com.ngsolutions.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int Type;
    Uri uri;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private float ZOOM = 13f;
    double finalLat,finalLong;
    Boolean isFirst = true;
    Button backBtn, nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        backBtn = findViewById(R.id.soilBack1Btn);
        nextBtn = findViewById(R.id.soilNext1Btn);

        uri = (Uri) getIntent().getExtras().get("imageUri");
        Type = getIntent().getExtras().getInt("Type");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapActivity.this,AnalysisActivity.class);
                i.putExtra("Lat",finalLat);
                i.putExtra("Long",finalLong);
                i.putExtra("imageUri",uri);
                i.putExtra("Type",Type);
                startActivity(i);
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if(!isFirst)
                    return;
                isFirst=false;
                LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f);
                mMap.animateCamera(cameraUpdate);
            }
        });
        Location location = mMap.getMyLocation();



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isFirst=false;
                finalLat = latLng.latitude;
                finalLong = latLng.longitude;

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                nextBtn.setEnabled(true);

                mMap.clear();
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        latLng, 15);
                mMap.animateCamera(location);
                mMap.addMarker(markerOptions);
            }
        });

        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        try{
            Task<Location> loc = fusedLocationProviderClient.getLastLocation();

            loc.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if(task.isSuccessful()){
                        if(task.getResult()!=null)
                        {
                            Toast.makeText(MapActivity.this, "Getting Accurate Location", Toast.LENGTH_SHORT).show();
                            Log.d("Nira",task.getResult().toString());
                            moveCamera(new LatLng(task.getResult().getLatitude(),task.getResult().getLongitude()),ZOOM);
                        }
                        else {
                            Toast.makeText(MapActivity.this, R.string.current_location_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }catch(Exception e){
            Toast.makeText(this, "Failed to get your location", Toast.LENGTH_SHORT).show();
        }


    }
    @SuppressLint("MissingPermission")
    private void moveCamera(LatLng latLng, float ZOOM)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM));
    }

}