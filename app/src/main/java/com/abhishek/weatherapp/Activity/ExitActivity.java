package com.abhishek.weatherapp.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.abhishek.weatherapp.databinding.ActivityExitBinding;
import com.bumptech.glide.Glide;

import java.util.Date;

public class ExitActivity extends AppCompatActivity {
    /**
     The activity that opens when user presses back from the main activity
     **/

    ActivityExitBinding binding;
    int currentTime = 0;
    String gifUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Generate current time
        Date date = new Date();
        currentTime = date.getHours();

        // logic to set the exit gif's URL using device's current time

        if (currentTime >= 0 && (currentTime <= 3 || currentTime >= 20)) {
            //night time
            gifUrl = "https://media.giphy.com/media/fteNljocmMGqlmiR3K/giphy.gif";
        } else {
            gifUrl = "https://media.giphy.com/media/1X8XwNVmlhnkBugSBZ/giphy.gif";
        }

        // view binding
        binding = ActivityExitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // using Glide to render the GIF in Image View
        Glide.with(this).asGif().load(gifUrl).into(binding.imageViewEnd);

        // intent to close the application after 5 seconds
        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        finish();
                    }
                },
                5000);
    }

    // code to remove the application from list of recent applications
    @Override
    public void finish() {
        super.finishAndRemoveTask();
    }

    // overriding the onBackPressed method so that the user can't press back from this screen
    @Override
    public void onBackPressed() {

    }
}