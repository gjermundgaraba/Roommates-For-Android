package com.gjermundbjaanes.apps.roommates.notloggedin.signup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.InputValidation;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;

@SuppressLint("DefaultLocale")
public class SignUpActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setUpLayout(savedInstanceState);
        setUpSignUpButton();
        setUpRepeatPasswordField();
    }

    private void setUpLayout(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);
    }

    private void setUpSignUpButton() {
        final Button signUp = (Button) findViewById(R.id.buttonRegistrer);
        signUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        signUp.setTextColor(Color.BLACK);
                        return true;
                    case MotionEvent.ACTION_UP:
                        signUp.setTextColor(0xFF9933CC);
                        signUp();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void setUpRepeatPasswordField() {
        EditText repeatPasswordField = (EditText) findViewById(R.id.editTextRptPassword_signup);
        repeatPasswordField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                signUp();
                return false;
            }
        });
    }

    private void signUp() {
        EditText displayNameEditText = (EditText) findViewById(R.id.editTextDisplayName_signup);
        EditText emailEditText = (EditText) findViewById(R.id.editTextEmail_signup);
        EditText passwordEditText = (EditText) findViewById(R.id.editTextPassword_signup);
        EditText repeatPasswordEditText = (EditText) findViewById(R.id.editTextRptPassword_signup);

        String displayName = displayNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim().toLowerCase();
        String password = passwordEditText.getText().toString();
        String repeatPassword = repeatPasswordEditText.getText().toString();

        if (displayName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                repeatPassword.isEmpty()) {
            ToastMaker.makeShortToast(R.string.all_fields_must_be_filled_out, this);
        } else if (!InputValidation.emailIsValid(email)) {
            ToastMaker.makeShortToast(R.string.email_not_valid, this);
        } else if (!InputValidation.passwordIsValid(password)) {
            ToastMaker.makeShortToast(R.string.password_not_valid, this);
        } else if (!password.equals(repeatPassword)) {
            ToastMaker.makeShortToast(R.string.passwords_dont_match, this);
            return;
        }

        User user = new User();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.setDisplayName(displayName);

        final ProgressDialog signUpProgress =
                ProgressDialog.show(this, getString(R.string.signing_up), getString(R.string.please_wait), true);
        user.signUpInBackground(new UserSignUpCallback(signUpProgress, this));
    }
}
