package com.ridewave.ridewave;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.Manifest;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.roger.catloadinglibrary.CatLoadingView;

import in.codeshuffle.typewriterview.TypeWriterView;

public class LoadingActivity extends AppCompatActivity {
    private String name,email;
    CatLoadingView mView;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_CODE);
        } else {
            getLastLocation();
        }
        getSupportActionBar().hide();
        //Create Object and refer to layout view
        TypeWriterView typeWriterView=(TypeWriterView)findViewById(R.id.typeWriterView);

        //Setting each character animation delay
        typeWriterView.setDelay(1);

        //Setting music effect On/Off
        typeWriterView.setWithMusic(false);

        //Animating Text
        typeWriterView.animateText("Welcome to Fikisha App.");

        // Using handler with postDelayed called runnable run method
        new Handler().postDelayed(() -> {

            Intent i = new Intent(LoadingActivity.this,HomeActivity.class);

            startActivity(i);

            // close this activity

            finish();

        }, 12*1000); // wait for 8 seconds
    }
    private void startLocationUpdates() {
        // TODO: Implement your location update logic here
        Toast.makeText(this, "Location updates started", Toast.LENGTH_SHORT).show();
    }



    private void showGPSDisabledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS is disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Open location settings
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        //zoomToCurrentLocation();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    String locationString = "Lat: " + location.getLatitude() + " , Long: " + location.getLongitude();
                    SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLatitude(String.valueOf(location.getLatitude()));
                    SharedPrefManager.getInstance(getApplicationContext()).saveDeviceLng(String.valueOf(location.getLongitude()));
                } else {
                    Toast.makeText(LoadingActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}