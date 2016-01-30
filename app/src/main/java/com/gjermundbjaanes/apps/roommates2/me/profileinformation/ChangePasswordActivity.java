package com.gjermundbjaanes.apps.roommates2.me.profileinformation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.gjermundbjaanes.apps.roommates2.R;
import com.gjermundbjaanes.apps.roommates2.helpers.ToastMaker;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class ChangePasswordActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_password);

        final EditText oldPasswordField = (EditText) findViewById(R.id.textOldPassword);
        final EditText newPasswordField = (EditText) findViewById(R.id.textNewPassword);
        final EditText repeatPasswordField = (EditText) findViewById(R.id.textRepeatPassword);

        Button changePasswordButton = (Button) findViewById(R.id.buttonChangePwd);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordField.getText().toString();
                String newPassword = newPasswordField.getText().toString();
                String repeatedPassword = repeatPasswordField.getText().toString();

                PasswordChanger passwordChanger = new PasswordChanger(oldPassword, newPassword, repeatedPassword);

                Context context = ChangePasswordActivity.this;
                ProgressDialog progressDialog = showProgressDialog(context);

                try {
                    passwordChanger.validate();
                    passwordChanger.performChange(new SaveChangePasswordCallback(progressDialog, context));
                } catch (ParseException e) {
                    progressDialog.dismiss();
                    ToastMaker.makeLongToast(e.getMessage(), context);
                }

            }
        });
    }

    private ProgressDialog showProgressDialog(Context context) {
        return ProgressDialog.show(context, context.getString(R.string.changing_password),
                context.getString(R.string.please_wait), true);
    }


    private class SaveChangePasswordCallback extends SaveCallback {
        private ProgressDialog progressDialog;
        private Context context;

        public SaveChangePasswordCallback(ProgressDialog progressDialog, Context context) {
            this.progressDialog = progressDialog;
            this.context = context;
        }

        @Override
        public void done(ParseException e) {
            progressDialog.dismiss();
            if (e == null) {
                ToastMaker.makeLongToast(R.string.toast_change_password_success, context);
            } else {
                ToastMaker.makeLongToast(e.getMessage(), context);
            }

            ChangePasswordActivity.this.finish();
        }
    }
}