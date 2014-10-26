package com.realkode.roomates.NotLoggedIn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SignUpCallback;
import com.realkode.roomates.Helpers.InputValidation;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.MainActivity;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

@SuppressLint("DefaultLocale")
public class SignupActivity extends Activity {

	protected boolean   exists = true;
	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Set up UI
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signup);
		
		// Get UI-elements
		progressBar = (ProgressBar) findViewById(R.id.progressBar_Signup);
		final Button signup = (Button) findViewById(R.id.buttonRegistrer);
		EditText repeatPasswordField = (EditText) findViewById(R.id.editTextRptPassword_signup);
		
		signup.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					signup.setTextColor(Color.BLACK);
					return true; 
				case MotionEvent.ACTION_UP:
					signup.setTextColor(0xFF9933CC);
					signUp();
					return true;
				}
				return false;
			}
		});

		repeatPasswordField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                signUp();
                return false;
            }
        });
	}

	private void signUp() {
		// get all UI-elements
		EditText displayNameEditText = (EditText) findViewById(R.id.editTextDisplayName_signup);
		EditText emailEditText = (EditText) findViewById(R.id.editTextEmail_signup);
		EditText passwordEditText = (EditText) findViewById(R.id.editTextPassword_signup);
		EditText repeatPasswordEditText = (EditText) findViewById(R.id.editTextRptPassword_signup);
		
		// get the input and format it
		String displayName = displayNameEditText.getText().toString().trim();
		String email = emailEditText.getText().toString().trim().toLowerCase();
		String password = passwordEditText.getText().toString();
		String repeatPassword = repeatPasswordEditText.getText().toString();

        // Testing that fields are filled out correctly
		if (displayName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            ToastMaker.makeShortToast("All fields must be filled out", this);
			return;
		}

		if (!InputValidation.emailIsValid(email)) {
            ToastMaker.makeShortToast("Email address not valid", this);
			return;
		}

		if (!InputValidation.passwordIsValid(password)) {
            ToastMaker.makeShortToast("Password is not valid. " +
                    "A Valid password needs to be at least 6 characters long, " +
                    "have at least one upper and one lower case letters " +
                    "and at least one number", this);
			return;
		}

		if (!password.equals(repeatPassword)) {
            ToastMaker.makeShortToast("Passwords does not match", this);
			return;
		}
        User user = new User();
		user.setUsername(email);
		user.setPassword(password);
		user.setEmail(email);
		user.put("displayName", displayName);
		

		progressBar.setVisibility(View.VISIBLE);


        final ProgressDialog signUpProgress = ProgressDialog.show(this, "Signing Up" , " Please wait ... ", true);

        // Signing up the new user
		user.signUpInBackground(new SignUpCallback() {
			@Override
			public void done(ParseException e) {
                signUpProgress.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
				if (e == null) {
					ToastMaker.makeLongToast("New user successfully created", SignupActivity.this);
					startMain();
				} 
				else {
					if (e.getCode() == ParseException.USERNAME_TAKEN) {
                        ToastMaker.makeLongToast("E-mail already in use. Try resetting your password", SignupActivity.this);
						return;
					}
					if (e.getCode() == ParseException.EMAIL_TAKEN) {
                        ToastMaker.makeLongToast("E-mail already in use. Try resetting your password", SignupActivity.this);
						return;
					} 
					else
                        ToastMaker.makeLongToast("Something went wrong. Try again later", SignupActivity.this);
				}
			}
		});
	}

    // Start the mainActivity
	private void startMain() {
		Context context = this;
		Intent intent = new Intent(context, MainActivity.class);
		startActivity(intent);
	}
}
