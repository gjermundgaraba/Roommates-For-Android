package com.realkode.roomates.Me.ProfileInformation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.realkode.roomates.R;

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
        changePasswordButton.setOnClickListener(
                new ChangePasswordOnClickListener(oldPasswordField, newPasswordField, repeatPasswordField, this));
    }


}