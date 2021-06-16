package com.abhishek.weatherapp.Util;

import android.widget.EditText;

/**Class that contains the validation functions used throughout the application**/
public class Validation {

    // function to check EditText
    public static boolean isEditTextValid(EditText editText) {
        String str = editText.getText().toString();
        return !str.isEmpty();
    }
}
