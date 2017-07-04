package com.widiarifki.findtutor.helper;

/**
 * Created by widiarifki on 20/06/2017.
 */

public class FormInputChecker {

    public static boolean isEmailValid(String email){
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password, int minLength){
        return password.length() > minLength;
    }
}
