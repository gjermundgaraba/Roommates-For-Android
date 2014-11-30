package com.gjermundbjaanes.apps.roommates.me.profileinformation;

import android.content.Context;

import com.gjermundbjaanes.apps.roommates.App;
import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.InputValidation;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PasswordChanger {
    String oldPassword;
    String newPassword;
    String repeatedPassword;

    public PasswordChanger(String oldPassword, String newPassword, String repeatedPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.repeatedPassword = repeatedPassword;
    }

    public void validate() throws ParseException {
        Context context = App.getContext();
        User user = User.getCurrentUser();

        if (!newPassword.equals(repeatedPassword)) {
            throw new ParseException(context.getString(R.string.toast_change_password_dont_match), null);
        } else if (!InputValidation.passwordIsValid(newPassword)) {
            throw new ParseException(context.getString(R.string.toast_change_password_invalid), null);
        } else {
            try {
                ParseUser.logIn(user.getUsername(), oldPassword);
            } catch (ParseException e) {
                if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    throw new ParseException(context.getString(R.string.toast_change_password_wrong_password), null);
                } else {
                    throw e;
                }
            }
        }
    }

    public void performChange(SaveCallback saveCallback) {
        ParseUser.getCurrentUser().setPassword(newPassword);

        ParseUser.getCurrentUser().saveInBackground(saveCallback);
    }
}
