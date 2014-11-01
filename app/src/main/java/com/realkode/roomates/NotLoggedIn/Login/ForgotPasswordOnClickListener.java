package com.realkode.roomates.NotLoggedIn.Login;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.R;

public class ForgotPasswordOnClickListener implements DialogInterface.OnClickListener {
    Context context;
    EditText emailField;

    public ForgotPasswordOnClickListener(Context context, EditText emailField) {
        this.context = context;
        this.emailField = emailField;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String email = getEmail();

        final ProgressDialog resetProgress = getProgressDialog();

        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException parseException) {
                resetProgress.dismiss();

                if (parseException == null) {
                    ToastMaker.makeLongToast(R.string.reset_email_sent_with_instructions, context);
                } else {
                    ToastMaker.makeLongToast(parseException.getMessage(), context);
                }
            }
        });
    }

    private String getEmail() {
        return emailField.getText().toString().toLowerCase().trim();
    }

    private ProgressDialog getProgressDialog() {
        return ProgressDialog.show(context, context.getString(R.string.resetting_password),
                context.getString(R.string.please_wait), true);
    }
}
