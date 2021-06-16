package com.abhishek.weatherapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.abhishek.weatherapp.R;
import com.abhishek.weatherapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    //Data Members
    ActivityMainBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* binding and shared preference initialization */
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getApplicationContext().getSharedPreferences("WeatherAppPref", MODE_PRIVATE);

        // Setting up the bottom navigation bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**Overriding the back pressed method so that the ExitActivity opens when back is pressed**/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainActivity.this, ExitActivity.class));
    }
}