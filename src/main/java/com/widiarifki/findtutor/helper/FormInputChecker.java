package com.widiarifki.findtutor.helper;

import android.text.TextUtils;

/**
 * Created by widiarifki on 20/06/2017.
 */

public class FormInputChecker {

    public static boolean isEmpty(String input){
        return TextUtils.isEmpty(input);
    }

    public static boolean isEqual(String input, String compareInput){
        return input.equals(compareInput);
    }

    public static boolean isEmailValid(String email){
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password, int minLength){
        return password.length() > minLength;
    }
}
