package com.realkode.roomates.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {
    public static boolean passwordIsValid(String password) {
        Pattern pattern;
        Matcher matcher;

        /** Regex-string to check if password meets requirements. Tests to check that password contains
         * at least one digit (*\\d), at least one lower case letter (*[a-z]) and atleast one upper-case
         * letter (*[A-Z[]). Also the length has to be between 6 and 100 characters.
         */

        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,100})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean emailIsValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
