package com.realkode.roomates.Me;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.InputValidation;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;


/**
 * Activity to change password
 */
public class ChangePasswordActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final ProgressBar progressSpinner = (ProgressBar) findViewById(R.id.progressBar_ChangePassword);

        final User user = User.getCurrentUser();
        final String username = user.getUsername();
        final EditText oldPassword = (EditText) findViewById(R.id.textOldPassword);
        final EditText newPassword = (EditText) findViewById(R.id.textNewPassword);
        final EditText repeatPassword = (EditText) findViewById(R.id.textRepeatPassword);

        Button changePasswordButton = (Button) findViewById(R.id.buttonChangePwd);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (newPassword.getText().toString().equals(repeatPassword.getText().toString())) {

                    // Check password strength
                    if (InputValidation.passwordIsValid(newPassword.getText().toString())) {
                        progressSpinner.setVisibility(View.VISIBLE);
                        // Trying to log in to check if old password is correct
                        ParseUser.logInInBackground(username, oldPassword.getText().toString(), new LogInCallback() {
                            @Override
                            public void done(ParseUser user2, ParseException arg1) {
                                progressSpinner.setVisibility(View.INVISIBLE);
                                if (user2 != null)
                                    changePassword(newPassword.getText().toString());
                                else
                                    ToastMaker.makeLongToast("You entered the wrong password", getApplicationContext());
                            }
                        });
                    } else {
                        ToastMaker.makeLongToast("Password is not valid. " +
                                "A Valid password needs to be at least 6 characters long, " +
                                "have at least one upper and one lower case letter " +
                                "and at least one number", getApplicationContext());
                    }
                } else {
                    ToastMaker.makeLongToast("Passwords do not match", getApplicationContext());
                }
            }
        });
    }

    private void changePassword(String newPwd) {
        ParseUser.getCurrentUser().setPassword(newPwd);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e == null)
                    ToastMaker.makeLongToast("Password changed", getApplicationContext());
                else
                    ToastMaker.makeLongToast(e.getMessage(), getApplicationContext());
            }
        });
    }
}