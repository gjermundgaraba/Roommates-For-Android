package com.gjermundbjaanes.apps.roommates.notloggedin.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gjermundbjaanes.apps.roommates.MainActivity;
import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.ButtonOnTouchListener;
import com.gjermundbjaanes.apps.roommates.helpers.ParseCloudFunctionNames;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.notloggedin.signup.SignUpActivity;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.HashMap;

@SuppressLint("DefaultLocale")
public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                .setPositiveButton(getString(R.string.ok), new ResetPasswordOnClickListener(emailField))
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
            ToastMaker.makeLongToast(R.string.email_password_must_be_filled_out, this);
        }
    }

    private class ResetPasswordOnClickListener implements DialogInterface.OnClickListener {
        private final EditText emailField;

        public ResetPasswordOnClickListener(EditText emailField) {
            this.emailField = emailField;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            final Context context = LoginActivity.this;
            String email = emailField.getText().toString().toLowerCase().trim();

            if (email.isEmpty()) {
                ToastMaker.makeLongToast(getString(R.string.you_must_enter_an_email), context);
            } else {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("username", email);

                final ProgressDialog resetProgress = ProgressDialog.show(context, context.getString(R.string.resetting_password),
                        context.getString(R.string.please_wait), true);
                ParseCloud.callFunctionInBackground(ParseCloudFunctionNames.RESET_PASSWORD, params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object o, ParseException e) {
                        resetProgress.dismiss();
                        if (e == null) {
                            String response = (String)o;
                            ToastMaker.makeLongToast(response, context);
                        } else {
                            ToastMaker.makeLongToast(e.getMessage(), context);
                        }
                    }
                });
            }
        }
    }
}
