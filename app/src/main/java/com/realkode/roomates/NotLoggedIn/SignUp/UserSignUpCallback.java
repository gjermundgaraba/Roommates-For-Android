package com.realkode.roomates.NotLoggedIn.SignUp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseException;
import com.parse.SignUpCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.MainActivity;
import com.realkode.roomates.R;

public class UserSignUpCallback extends SignUpCallback {
    ProgressDialog progressDialog;
    Context context;

    public UserSignUpCallback(ProgressDialog progressDialog, Context context) {
        this.progressDialog = progressDialog;
        this.context = context;
    }

    @Override
    public void done(ParseException e) {
        progressDialog.dismiss();
        if (e == null) {
            ToastMaker.makeLongToast(R.string.sign_up_successful, context);
            startMainActivity();
        } else {
            switch (e.getCode()) {
                case ParseException.USERNAME_TAKEN:
                    ToastMaker.makeLongToast(R.string.username_email_taken, context);
                    break;
                case ParseException.EMAIL_TAKEN:
                    ToastMaker.makeLongToast(R.string.email_taken, context);
                    break;
                default:
                    ToastMaker.makeLongToast(R.string.something_went_wrong, context);
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
