package com.realkode.roomates.NotLoggedIn.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.*;
import com.realkode.roomates.Helpers.ButtonOnTouchListener;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.MainActivity;
import com.realkode.roomates.NotLoggedIn.SignUp.SignUpActivity;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

@SuppressLint("DefaultLocale")
public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        if (User.someoneIsLoggedIn()) {
            startMainActivity();
        }

        super.onCreate(savedInstanceState);

        setUpLayout();
        setUpButtons();
    }

    protected void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUpButtons() {
        Button login = (Button) findViewById(R.id.buttonLogin);
        Button signUp = (Button) findViewById(R.id.buttonSignup);
        Button facebookLogin = (Button) findViewById(R.id.facebookLoginButton);
        Button resetPassword = (Button) findViewById(R.id.buttonForgot);
        EditText passwordField = (EditText) findViewById(R.id.editTextPassword);

        signUp.setOnTouchListener(new ButtonOnTouchListener(new ButtonOnTouchListener.TouchActionHandler() {
            @Override
            public void performAction() {
                startSignUpActivity();
            }
        }));

        login.setOnTouchListener(new ButtonOnTouchListener(new ButtonOnTouchListener.TouchActionHandler() {
            @Override
            public void performAction() {
                loginUser();
            }
        }));

        facebookLogin.setOnTouchListener(new ButtonOnTouchListener(new ButtonOnTouchListener.TouchActionHandler() {
            @Override
            public void performAction() {
                new FacebookLogin(LoginActivity.this, (Button) findViewById(R.id.facebookLoginButton), LoginActivity.this).startFacebookLogin();
            }
        }));

        resetPassword.setOnTouchListener(new ButtonOnTouchListener(new ButtonOnTouchListener.TouchActionHandler() {
            @Override
            public void performAction() {
                viewResetPasswordDialog();
            }
        }));

        passwordField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                loginUser();
                return false;
            }
        });
    }

    private void setUpLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
    }

    // The result for facebookloginactivity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    private void viewResetPasswordDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText emailField = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder.setTitle(getString(R.string.forgot_password))
                .setMessage(getString(R.string.enter_your_email))
                .setView(promptsView)
                .setPositiveButton(getString(R.string.ok), new ForgotPasswordOnClickListener(this, emailField))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

    }

    private void startSignUpActivity() {
        Context context = this;
        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
    }

    private void loginUser() {
        final Button loginButton = (Button) findViewById(R.id.buttonLogin);
        EditText emailField = (EditText) findViewById(R.id.editTextEmail);
        EditText passwordField = (EditText) findViewById(R.id.editTextPassword);
        String email = emailField.getText().toString().toLowerCase().trim();
        String password = passwordField.getText().toString();

        if (!email.isEmpty() && password != null && !password.isEmpty()) {
            loginButton.setClickable(false);
            loginButton.setEnabled(false);
            final ProgressDialog loginProgress =
                    ProgressDialog.show(LoginActivity.this, getString(R.string.logging_in), getString(R.string.please_wait), true);
            ParseUser.logInInBackground(email, password, new UserLogInCallback(this, this, loginButton, loginProgress));
        } else {
            ToastMaker.makeLongToast(getString(R.string.email_password_must_be_filled_out), this);
        }
    }
}
