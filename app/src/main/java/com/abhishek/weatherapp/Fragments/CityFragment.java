package com.abhishek.weatherapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abhishek.weatherapp.R;
import com.abhishek.weatherapp.Util.APIKey;
import com.abhishek.weatherapp.databinding.CityFragmentBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

public class CityFragment extends Fragment {

    //ViewBinding and SharedPreferences
    CityFragmentBinding binding;
    SharedPreferences sharedPreferences;

    // Data Members to handle the spinner and Date
    String unit = "";
    String[] cities = {"Delhi", "Mumbai", "Noida"};
    Double[] latitudeCities = {28.7041d, 19.0760d, 28.5355d};
    Double[] longitudeCities = {77.1025d, 72.8777d, 77.3910d};
    int spinnerPosition = 0;
    String daySelected = "";
    String dateSelected = "";
    Double latitude, longitude;

    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //initializing the binding and sharedPreference
        binding = CityFragmentBinding.inflate(inflater, container, false);
        sharedPreferences = getContext().getSharedPreferences("WeatherAppPref", Context.MODE_PRIVATE);

        //fetching the unit type from sharedPreference
        unit = getItemFromSharedPreference("units");
        //fetching the user name from sharedPreference
        binding.userName.setText(getItemFromSharedPreference("userName"));
        String un = unit.equals("metric") ? "°C" : "°F";
        binding.textUnit.setText(un);

        setViews();
        getLocationFromSharedPreference();
        return binding.getRoot();
    }

    /**Fetching and setting the data members to latitude & longitude**/
    public void getLocationFromSharedPreference() {
        double defLocation = 0.0;
        latitude = (double) (sharedPreferences.getFloat("latitude", (float) defLocation));
        longitude = (double) (sharedPreferences.getFloat("longitude", (float) defLocation));
    }

    /**Function to get value of a key from shared Preference**/
    public String getItemFromSharedPreference(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**Function to set the views of the fragment**/
    void setViews() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);

        long endOfMonth = calendar.getTimeInMillis();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -4);

        long startOfMonth = calendar.getTimeInMillis();

        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd");

        daySelected = dateFormat.format(d);

        binding.calender.setMaxDate(endOfMonth);
        binding.calender.setMinDate(startOfMonth);

        /**Reason for limiting the date range for the calendar -
         * Open Weather API only allows the call to be in a
         * specific range under free plan**/

        dateSelected = String.valueOf(binding.calender.getDate()).substring(0, 10);

        binding.calender.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String str = Month.of(month + 1) + " " + dayOfMonth + " " + year;
            SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");
            // the day selected in the calendar
            daySelected = dayOfMonth + "";

            Date date = null;
            try {
                date = df.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateSelected = String.valueOf(date.getTime() / 1000L);
            // the date selected in the calendar
        });

        // setting the spinner with the values
        setSpinner();

        // on click on the button
        binding.btnReport.setOnClickListener(v -> {
            // finding the selected value of latitude and longitude accord to index selected using the spinner
            double latitude = latitudeCities[spinnerPosition];
            double longitude = longitudeCities[spinnerPosition];
            makeAPICall(latitude, longitude, dateSelected, cities[spinnerPosition]);
        });

    }

    /**Function to make the APi Call to fetch the results**/
    public void makeAPICall(double latitude, double longitude, String date, String city) {
        Calendar calendarNew = Calendar.getInstance();
        Date dNew = calendarNew.getTime();
        DateFormat dateFormatNew = new SimpleDateFormat("dd");

        int daySelected = Integer.parseInt(this.daySelected);
        int dayToday = Integer.parseInt(dateFormatNew.format(dNew));

        // code to scroll to the new generated card in the scroll view
        binding.cardLayout.getParent().requestChildFocus(binding.cardLayout, binding.cardLayout);

        if (daySelected < dayToday) {
            // the date selected in the is before than the current date
            getResultFromAPIPastDays(latitude, longitude, date, city);
        } else {
            // the date selected is current date or ahead
            getResultFromAPIFutureDays(latitude, longitude, daySelected - dayToday, city);
        }
    }

    /**Function to set the spinner with the city array**/
    public void setSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.cityDropDown.setAdapter(adapter);
        binding.cityDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // update the position data variable on the spinner item selected
                spinnerPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    /**Function to get result from API if of past days**/
    public void getResultFromAPIPastDays(Double latitude, Double longitude, String date, String city) {

        // API Fetch call using retrofit
        String LAT_LONG = "https://api.openweathermap.org/data/2.5/onecall/timemachine?lat=" + latitude + "&lon=" + longitude + "&dt=" + date + "&appid=" + APIKey.APIKEY + "&units=" + unit;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, LAT_LONG, response -> {
            try {
                // object received
                JSONObject jsonObject = new JSONObject(response);
                // extracting the current JSON object
                JSONObject currentObject = jsonObject.getJSONObject("current");

                // getting results like windSpeed, temperature, humidity, etc.
                String wind_speed = currentObject.getString("wind_speed");
                String temp = currentObject.getString("temp");
                String humidity = currentObject.getString("humidity");
                String visibility = currentObject.getString("visibility");

                // code to show the result card layout
                binding.cardLayout.setVisibility(View.VISIBLE);

                //code to display the fetched data from the API
                binding.tempText.setText(Float.valueOf(temp).intValue() + "");
                binding.textHumidity.setText(humidity + "%");
                binding.textVisibility.setText(Float.valueOf(visibility).intValue() / 1000 + "km");
                binding.textWind.setText(wind_speed + "km/hr");
                binding.locationText.setText(city);
                binding.footerVisibility.setText("Visibility");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Toast.makeText(getContext(), error.getMessage() + " errorr ", Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    /**Function to get result from API if of current or future day**/
    public void getResultFromAPIFutureDays(Double latitude, Double longitude, int day, String city) {

        String JSON_URL = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&exclude=hourly,minutely" + "&appid=" + APIKey.APIKEY + "&units=" + unit;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL, response -> {
            try {
                // object received
                JSONObject jsonObject = new JSONObject(response);
                // extracting the current JSON object
                JSONArray jsonArray = jsonObject.getJSONArray("daily");
                JSONObject jsonObjectData = jsonArray.getJSONObject(day);

                //converting the JsonArray in day to JSONObject
                JSONObject objectWeather = jsonObjectData.getJSONArray("weather").getJSONObject(0);

                // code to fetch the image of weather using the weather object
                String imgCode = objectWeather.getString("icon");
                String url = "https://openweathermap.org/img/wn/" + imgCode + "@2x.png";

                // code to show the result card layout
                binding.cardLayout.setVisibility(View.VISIBLE);

                Picasso.get().load(url).into(binding.imageWeather);

                // getting results like windSpeed, temperature, humidity, etc.
                JSONObject main = jsonObjectData.getJSONObject("temp");
                String temp = main.getString("day");
                String humidity = jsonObjectData.getString("humidity");
                String wind_speed = jsonObjectData.getString("wind_speed");
                String clouds = jsonObjectData.getString("clouds");

                //code to display the fetched data from the API
                binding.locationText.setText(city);
                binding.imgVisibility.setBackground(getResources().getDrawable(R.drawable.cloud_001));
                binding.tempText.setText(Float.valueOf(temp).intValue() + "");
                binding.textHumidity.setText(humidity + "%");
                binding.footerVisibility.setText("Clouds");
                binding.textVisibility.setText(clouds);
                binding.textWind.setText(wind_speed + "\nkm/hr");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
