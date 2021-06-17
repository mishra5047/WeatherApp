package com.abhishek.weatherapp.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.abhishek.weatherapp.Activity.MainActivity;
import com.abhishek.weatherapp.Activity.SplashScreen;
import com.abhishek.weatherapp.Util.APIKey;
import com.abhishek.weatherapp.databinding.HomeFragmentBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    //ViewBinding and SharedPreferences
    HomeFragmentBinding binding;
    SharedPreferences sharedPreferences;

    //Data members to handle location
    // location data members
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    Double latitude, longitude;

    String unit = "";
    String suffixUnit = "";

    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //initializing the binding and sharedPreference
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        sharedPreferences = getContext().getSharedPreferences("WeatherAppPref", Context.MODE_PRIVATE);

        //fetching the unit type from sharedPreference
        binding.userName.setText(getItemFromSharedPreference("userName"));
        unit = getItemFromSharedPreference("units");
        //fetching the user name from sharedPreference
        suffixUnit = unit.equals("metric") ? "°C" : "°F";
        binding.textUnit.setText(suffixUnit);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getCurrentLocation();

        return binding.getRoot();
    }

    public void getCurrentLocation() {
        Context context = getContext();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // permission hasn't been granted
            setDefaultLocation();
            updateSharedPreference();
            getLocationFromSharedPreference();
            getResultFromAPI();
        } else {
            // Permission has been granted
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {

                            // Fetching location using fusedLocationClient
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            //Toast.makeText(getContext(), latitude+" $ " + longitude, Toast.LENGTH_SHORT).show();

                            /**If location isn't fetched then set the default latitude and longitude **/
                            if (latitude == null) {
                                setDefaultLocation();
                            }

                            // updating the shared preference with latitude and longitude
                            updateSharedPreference();
                        }

                        getLocationFromSharedPreference();
                        getResultFromAPI();
                    });
        }

    }

    /**Function to set the default latitude and longitude**/
    void setDefaultLocation() {
        latitude = 28.667823;
        longitude = 77.114950;
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

    /**Function to get value of a key from shared Preference**/
    public String getItemFromSharedPreference(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**Fetching and setting the data members to latitude & longitude**/
    public void getLocationFromSharedPreference() {
        Double defLocation = 0.0;
        latitude = (double) (sharedPreferences.getFloat("latitude", defLocation.floatValue()));
        longitude = (double) (sharedPreferences.getFloat("longitude", defLocation.floatValue()));
    }

    /**Function to generate Current Date**/
    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd");
        return new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()) + ", " + new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + dateFormat.format(date) + "th" + " " + (date.getYear() + 1900);
    }

    /**Function to get the result generated by API**/
    public void getResultFromAPI() {
        // API call to get the location of current user
        String resultCall = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + APIKey.APIKEY + "&units=" + unit;
        StringRequest stringRequestFirst = new StringRequest(Request.Method.GET, resultCall, response -> {
            try {
                // object received
                JSONObject jsonObject = new JSONObject(response);
                // extracting the current JSON object
                String city = jsonObject.getString("name");
                binding.locationText.setText(city);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue requestQueueOne = Volley.newRequestQueue(getContext());
        requestQueueOne.add(stringRequestFirst);

        //API call to get the other weather parameter of current location
        String LAT_LONG = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&appid=" + APIKey.APIKEY + "&units=" + unit;
        StringRequest stringRequestTwo = new StringRequest(Request.Method.GET, LAT_LONG, response -> {
            try {
                // object received
                JSONObject jsonObject = new JSONObject(response);
                jsonObject = jsonObject.getJSONObject("current");

                // extracting the current JSON object
                JSONObject objectWeather = jsonObject.getJSONArray("weather").getJSONObject(0);

                // code to fetch the image of weather using the weather object
                String imgCode = objectWeather.getString("icon");
                String url = "https://openweathermap.org/img/wn/" + imgCode + "@4x.png";

                // getting results like windSpeed, temperature, humidity, etc.
                String wind_speed = jsonObject.getString("wind_speed");
                String temp = jsonObject.getString("temp");
                String feelsLike = jsonObject.getString("feels_like");
                String pressure = jsonObject.getString("pressure");

                // finding the level of UVI using the result from API
                int uvi = jsonObject.getInt("uvi");
                String uviResult = "";
                // uvi ranges
                if (uvi >= 0 && uvi <= 2) {
                    uviResult = "Low";
                } else if (uvi >= 3 && uvi <= 5) {
                    uviResult = "Moderate";
                } else if (uvi >= 6 && uvi <= 7) {
                    uviResult = "High";
                } else {
                    uviResult = "Very High";
                }

                String humidity = jsonObject.getString("humidity");
                String visibility = jsonObject.getString("visibility");

                //making the views visible and progress bar invisible
                binding.layoutItems.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                Picasso.get().load(url).into(binding.imageWeather);

                //code to display the fetched data from the API
                binding.textFeelsLike.setText(feelsLike + " " + suffixUnit);
                binding.textPressure.setText(pressure + " hPa");
                binding.textUV.setText(uviResult);
                binding.textDate.setText(getCurrentDate());
                binding.textWind.setText(wind_speed + " km/hr");

                binding.tempText.setText(Float.valueOf(temp).intValue() + "");
                binding.textVisibility.setText(((Float.valueOf(visibility).intValue()) / 1000) + " km");
                binding.textHumidity.setText(humidity + " %");

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());

        RequestQueue requestQueueTwo = Volley.newRequestQueue(getContext());
        requestQueueTwo.add(stringRequestTwo);

    }
}