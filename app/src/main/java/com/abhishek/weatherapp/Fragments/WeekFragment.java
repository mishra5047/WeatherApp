package com.abhishek.weatherapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abhishek.weatherapp.Adapters.ItemWeatherReport;
import com.abhishek.weatherapp.Adapters.WeatherReportAdapter;
import com.abhishek.weatherapp.Util.APIKey;
import com.abhishek.weatherapp.databinding.WeekFragmentBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekFragment extends Fragment {

    // View Binding and SharedPreferences
    WeekFragmentBinding binding;
    SharedPreferences sharedPreferences;

    // location data members
    Double latitude, longitude;
    String unit = "";

    // adapter and array list for Weather Recycler View
    WeatherReportAdapter adapter;
    ArrayList<ItemWeatherReport> list;

    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        //initializing the binding and sharedPreference
        binding = WeekFragmentBinding.inflate(inflater, container, false);
        list = new ArrayList<>();
        sharedPreferences = getContext().getSharedPreferences("WeatherAppPref", Context.MODE_PRIVATE);
        unit = getItemFromSharedPreference("units");

        //fetching the user name from sharedPreference
        binding.userName.setText(getItemFromSharedPreference("userName"));

        getLocationFromSharedPreference();

        networkCall();
        return binding.getRoot();
    }

    /**Function to fetch the result from API**/
    public void networkCall() {

        String unitMetric = unit.equals("metric") ? "°C" : "°F";

        String JSON_URL = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&exclude=hourly,minutely" + "&appid=" + APIKey.APIKEY + "&units=" + unit;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // object received
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("daily");
                    if (jsonArray.length() > 1) {

                        // indicates that valid call is made and data is received
                        // hiding the progressBar and displaying the result layout
                        binding.progressBar.setVisibility(View.GONE);
                        binding.layoutItems.setVisibility(View.VISIBLE);

                        //clear the arraylist
                        list.clear();

                        for (int i = 1; i < jsonArray.length(); i++) {

                            // object received
                            JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                            JSONObject objectWeather = jsonObjectData.getJSONArray("weather").getJSONObject(0);

                            // code to fetch the image of weather using the weather object
                            String imgCode = objectWeather.getString("icon");
                            String urlWeather = "https://openweathermap.org/img/wn/" + imgCode + "@2x.png";
                            String dataWeather = objectWeather.getString("main");

                            // getting results like tempMin, tempMax, etc.
                            JSONObject main = jsonObjectData.getJSONObject("temp");
                            String temp_min = main.getString("min");
                            String temp_max = main.getString("max");

                            if (i == 2) {

                                // Setting the tomorrow display card above and displaing the data
                                binding.textHumidity.setText(jsonObjectData.getString("humidity") + " %");
                                binding.textWind.setText(jsonObjectData.getString("wind_speed") + " km/hr");
                                binding.textPressure.setText(jsonObjectData.getString("pressure") + " Pa");
                                binding.maxTemp.setText(Float.valueOf(temp_max).intValue() + "");
                                binding.minTemp.setText(Float.valueOf(temp_min).intValue() + "");
                                binding.textUnitMax.setText(unitMetric);
                                binding.textUnitMin.setText(unitMetric);
                                Picasso.get().load(urlWeather).into(binding.imageWeather);
                            }

                            /**Calendar instance to generate the date**/
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, i);
                            Date date = calendar.getTime();
                            DateFormat dateFormat = new SimpleDateFormat("dd");
                            String day = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());

                            // adding the item of itemWeather to the arraylist to display in the adapter
                            list.add(new ItemWeatherReport(Float.valueOf(temp_min).intValue() + "", Float.valueOf(temp_max).intValue() + "", urlWeather, day, dataWeather));
                        }

                        // setting the adapter for the recycler View
                        adapter = new WeatherReportAdapter(list, getContext(), unitMetric);
                        // setting the recycler view layout
                        binding.recWeekReport.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.recWeekReport.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
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

}
