package com.abhishek.weatherapp.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.abhishek.weatherapp.Util.Validation;
import com.abhishek.weatherapp.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class SplashScreen extends AppCompatActivity {
    /**
     The Splash Screen activity that's the default activity for the application
     **/

    // View Binding and SharedPreferences
    ActivitySplashScreenBinding binding;
    SharedPreferences sharedPreferences;

    // location data members
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    Double latitude, longitude;

    /** To check whether the user has already opened the app earlier**/
    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getApplicationContext().getSharedPreferences("WeatherAppPref", MODE_PRIVATE);

        //check if shared preference contains the key userName
        String res = getItemFromSharedPreference("userName");

        if (!res.isEmpty()) {
            //user has already logged int earlier intent for the home screen
            getCurrentLocation();
            intentUtil();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //location fetch done here
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

        //attaching click listener to the button
        binding.continueBtn.setOnClickListener(v -> {

            // function written in Validation to check if EditText's Text is empty or not
            boolean res = Validation.isEditTextValid(binding.nameEnter);

            if (res) {
                // Not Empty
                // update the shared preferences and go to the next screen
                initSharedPreference(binding.nameEnter.getText().toString());
                intentUtil();
            } else {
                // Empty
                binding.nameEnter.setError("Can't Be Empty");
                binding.nameEnter.requestFocus();
            }
        });
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

    /**Function to check if user has given the location permission or not**/
    void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                /** Alert Dialogue Box to Ask the user for location permission */
                new android.app.AlertDialog.Builder(SplashScreen.this)
                        .setTitle("Required Location Permission")
                        .setMessage("Grant Permission To Use Current Location, Else default Location will be used")
                        .setPositiveButton("ok", (dialogInterface, i) -> {
                            ActivityCompat.requestPermissions(SplashScreen.this,
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
                ActivityCompat.requestPermissions(SplashScreen.this,
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
                    .addOnSuccessListener(SplashScreen.this, location -> {
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

    /**Function to set the default latitude and longitude**/
    void setDefaultLocation() {
        latitude = 28.667823;
        longitude = 77.114950;
    }

    /**Intent function for MainActivity.class**/
    public void intentUtil() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    /**Function to edit the shared Preference**/
    public void initSharedPreference(String str) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", str);
        editor.putString("units", "metric");
        editor.apply();
    }

    /**Function to get value of a key from shared Preference**/
    public String getItemFromSharedPreference(String key) {
        return sharedPreferences.getString(key, "");
    }
}