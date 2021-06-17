package com.abhishek.weatherapp.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.abhishek.weatherapp.R;
import com.abhishek.weatherapp.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    //Data Members
    ActivityMainBinding binding;
    SharedPreferences sharedPreferences;

    // location data members
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* binding and shared preference initialization */
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getApplicationContext().getSharedPreferences("WeatherAppPref", MODE_PRIVATE);

        //location fetch done here
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

        // Setting up the bottom navigation bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**Function to check if user has given the location permission or not**/
    void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                /** Alert Dialogue Box to Ask the user for location permission */
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Required Location Permission")
                        .setMessage("Grant Permission To Use Current Location, Else default Location will be used")
                        .setPositiveButton("ok", (dialogInterface, i) -> {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            // permission granted
                            getCurrentLocation();
                        })
                        .setNegativeButton("cancel", (dialogInterface, i) -> {
                            // permission not granted
                            dialogInterface.dismiss();
                            getCurrentLocation();
                        }).create().show();

            } else {
                // request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            }
        }
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // permission hasn't been granted
            setDefaultLocation();
            updateSharedPreference();
            return;
        } else {
            // Permission has been granted

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MainActivity.this, location -> {
                        if (location != null) {

                            // Fetching location using fusedLocationClient
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            /**If location isn't fetched then set the default latitude and longitude **/
                            if (latitude == null) {
                                setDefaultLocation();
                            }

                            // updating the shared preference with latitude and longitude
                            updateSharedPreference();
                        }
                    });
        }
    }

    /**Function to update the value of latitude & longitude in sharedPreference**/
    void updateSharedPreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (latitude == null) {
            setDefaultLocation();
        }
        editor.putFloat("latitude", latitude.floatValue());
        editor.putFloat("longitude", longitude.floatValue());
        editor.apply();
    }

    /**Function to set the default latitude and longitude**/
    void setDefaultLocation() {
        latitude = 28.667823;
        longitude = 77.114950;
    }


    /**Overriding the back pressed method so that the ExitActivity opens when back is pressed**/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainActivity.this, ExitActivity.class));
    }
}