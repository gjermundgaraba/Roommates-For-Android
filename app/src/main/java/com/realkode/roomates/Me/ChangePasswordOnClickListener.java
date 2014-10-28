package com.realkode.roomates.Me;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.InputValidation;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

class ChangePasswordOnClickListener implements View.OnClickListener {
    private final EditText oldPasswordField;
    private final EditText newPasswordField;
    private final EditText repeatPasswordField;
    private final Activity activity;
    private final Context context;

    ChangePasswordOnClickListener(EditText oldPasswordField, EditText newPasswordField, EditText repeatPasswordField,
                                  Activity activity) {
        this.oldPasswordField = oldPasswordField;
        this.newPasswordField = newPasswordField;
        this.repeatPasswordField = repeatPasswordField;
        this.activity = activity;
        this.context = activity;
    }

    @Override
    public void onClick(View v) {
        final String newPassword = newPasswordField.getText().toString();
        final String oldPassword = oldPasswordField.getText().toString();
        final String repeatPassword = repeatPasswordField.getText().toString();
        final String username = User.getCurrentUser().getUsername();

        if (newPassword.equals(repeatPassword)) {

            if (InputValidation.passwordIsValid(newPassword)) {
                final ProgressDialog loginProgress = ProgressDialog
                        .show(context, context.getString(R.string.progress_bar_change_password_login_title),
                                context.getString(R.string.progress_bar_change_password_login_message), true);

                // Log in to check if old password is correct
                ParseUser.logInInBackground(username, oldPassword, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException exception) {
                        loginProgress.dismiss();
                        if (user != null) {
                            changePassword(newPassword);
                        } else {
                            ToastMaker.makeLongToast(R.string.toast_change_password_wrong_password, context);
                        }
                    }
                });
            } else {
                ToastMaker.makeLongToast(R.string.toast_change_password_invalid, context);
            }
        } else {
            ToastMaker.makeLongToast(R.string.toast_change_password_dont_match, context);
        }


    }

    private void changePassword(String newPassword) {
        ParseUser.getCurrentUser().setPassword(newPassword);


        final ProgressDialog changeProgress = ProgressDialog
                .show(context, context.getString(R.string.progress_dialog_change_password_title),
                        context.getString(R.string.progress_dialog_change_password_message), true);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

            public void done(ParseException exception) {
                changeProgress.dismiss();
                if (exception == null) {
                    ToastMaker.makeLongToast(R.string.toast_change_password_success, context);
                    activity.finish();
                } else {
                    ToastMaker.makeLongToast(exception.getMessage(), context);
                }
            }
        });
    }
}
