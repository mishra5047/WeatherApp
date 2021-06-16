package com.abhishek.weatherapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abhishek.weatherapp.R;
import com.abhishek.weatherapp.Util.Validation;
import com.abhishek.weatherapp.databinding.SettingsFragmentBinding;

public class SettingsFragment extends Fragment {
    //ViewBinding and SharedPreferences
    SettingsFragmentBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //initializing the binding and sharedPreference
        binding = SettingsFragmentBinding.inflate(inflater, container, false);
        sharedPreferences = getContext().getSharedPreferences("WeatherAppPref", Context.MODE_PRIVATE);

        //fetching the user name from sharedPreference
        binding.userName.setText(getItemFromSharedPreference("userName"));

        setRadioGroup();

        // displaying the edit text, on click of edit image
        binding.editUserName.setOnClickListener(v -> {
            binding.inputUserName.setVisibility(View.VISIBLE);
        });

        // save settings button
        binding.btnSettings.setOnClickListener(v -> {
            if (binding.inputUserName.getVisibility() == View.VISIBLE) {
                // function written in Validation to check if EditText's Text is empty or not
                boolean res = Validation.isEditTextValid(binding.inputUserName);

                if (res) {
                    // Not Empty
                    // update the shared preferences and go to the next screen
                    addValueInSharedPreference("userName", binding.inputUserName.getText().toString());
                } else {
                    // Empty
                    binding.inputUserName.setError("Can't Be Empty");
                    binding.inputUserName.requestFocus();
                    return;
                }
            }

            // finding the selected button in the radio group
            int selectedBtn = binding.radioGroup.getCheckedRadioButtonId();

            String units = "";
            RadioButton selectedRadioButton = binding.getRoot().findViewById(selectedBtn);

            // updating the shared preference based on units
            if (selectedRadioButton.getText().toString().equals("Celsius")) {
                units = "metric";
            } else {
                units = "imperial";
            }

            addValueInSharedPreference("units", units);
            Toast.makeText(getContext(), "Settings Updated", Toast.LENGTH_LONG).show();
        });

        return binding.getRoot();
    }

    /** Function to set the radio group based on the value stored in the sharedPreference **/
    private void setRadioGroup() {
        // metric -> celsius
        // imperial -> Fahrenheit
        String res = getItemFromSharedPreference("units");
        if (res.equals("metric")) {
            // marking the Celsius radio button as selected
            binding.radioGroup.check(R.id.radioCelsius);
        } else {
            // marking the Fahrenheit radio button as selected
            binding.radioGroup.check(R.id.radioFahrenheit);
        }
    }

    /**Function to add a key value pair in Shared Preference**/
    public void addValueInSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**Function to get value of a key from shared Preference**/
    public String getItemFromSharedPreference(String key) {
        return sharedPreferences.getString(key, "");
    }
}
