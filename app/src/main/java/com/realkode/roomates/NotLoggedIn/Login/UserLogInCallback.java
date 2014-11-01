package com.realkode.roomates.NotLoggedIn.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class UserLogInCallback extends LogInCallback {
    LoginActivity loginActivity;
    Context context;
    Button loginButton;
    ProgressDialog progressDialog;

    public UserLogInCallback(LoginActivity loginActivity, Context context, Button loginButton, ProgressDialog progressDialog) {
        this.loginActivity = loginActivity;
        this.context = context;
        this.loginButton = loginButton;
        this.progressDialog = progressDialog;
    }

    @Override
    public void done(ParseUser user, ParseException e) {
        progressDialog.dismiss();
        if (e == null) {
            User.refreshChannels();
            loginActivity.startMainActivity();
        } else {
            loginButton.setClickable(true);
            loginButton.setEnabled(true);

            if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                ToastMaker.makeLongToast(R.string.wrong_email_password, context);
            } else {
                ToastMaker.makeLongToast(R.string.something_went_wrong, context);
            }
        }
    }
}
