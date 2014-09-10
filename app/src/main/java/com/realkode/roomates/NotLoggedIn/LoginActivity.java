package com.realkode.roomates.NotLoggedIn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;

import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;

import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.realkode.roomates.FacebookProfilePictureDownloader;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.MainActivity;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.Arrays;
import java.util.List;


@SuppressLint("DefaultLocale")
public class LoginActivity extends Activity {

    private ProgressBar pgb;
    private String facebookID;
    private ProgressDialog progressDialog;

    // Called when activity is created
    @Override
    public void onCreate(Bundle savedInstanceState) {

        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();


        ParseUser currentUser = ParseUser.getCurrentUser();
        System.out.println("Current user: " + currentUser);

        // Check if we are logged in or not.
        if (currentUser != null) {
            startMain();
        }

        // Set up some layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pgb = (ProgressBar) findViewById(R.id.progressBar_Login);
        addListenerOnButtons();
    }

    // The result for facebookloginactivity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }


    // Adding the button listeners and their method calls
    private void addListenerOnButtons() {

        final Button login = (Button) findViewById(R.id.buttonLogin);
        final Button signup = (Button) findViewById(R.id.buttonSignup);
        final Button facebookLogin = (Button) findViewById(R.id.facebookLoginButton);
        final Button resetPassword = (Button) findViewById(R.id.buttonForgot);
        EditText passwordField = (EditText) findViewById(R.id.editTextPassword);

        signup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        signup.setTextColor(Color.BLACK);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED CLICK
                        signup.setTextColor(0xFFFFFFFF);
                        signup();
                        return true;
                }
                return false;
            }
        });

        login.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        login.setTextColor(Color.BLACK);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED CLICK
                        login.setTextColor(0xFFFFFFFF);
                        loginuser();
                        return true;
                }
                return false;
            }
        });

        facebookLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        login.setTextColor(Color.BLACK);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED CLICK
                        login.setTextColor(0xFFFFFFFF);
                        facebookLogin();
                        return true;
                }
                return false;
            }
        });

        resetPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        resetPassword.setTextColor(Color.BLACK);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED CLICK
                        resetPassword.setTextColor(0xFFFFFFFF);
                        forgotPassword();

                        return true;
                }
                return false;
            }
        });

        passwordField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                loginuser();
                return false;

            }
        });

    }
    // The forgot password dialog
    private void forgotPassword() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder.setTitle("Forgot password").setMessage("Enter your e-mail")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        String email = userInput.getText().toString().toLowerCase().trim();
                        final ProgressDialog resetProgress = ProgressDialog.show(LoginActivity.this, "Resetting Password" , " Please wait ... ", true);
                        ParseUser.requestPasswordResetInBackground(email,
                                new RequestPasswordResetCallback() {
                                    public void done(ParseException e) {
                                        resetProgress.dismiss();
                                        if (e == null) {
                                            ToastMaker.makeLongToast("An e-mail was successfully sent with reset instructions",getApplicationContext());
                                            // An email was successfully sent with reset instructions.
                                        } else {
                                            ToastMaker.makeLongToast(e.getMessage(),getApplicationContext());
                                            System.out.println(e.getMessage());
                                            // Something went wrong. Look at the ParseException to see what's up.
                                        }
                                    }
                                }
                        );


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // show it
        alertDialog.show();

    }

    // Start the sign up activity
    private void signup() {
        Context context = this;
        Intent intent = new Intent(context, SignupActivity.class);
        startActivity(intent);
    }

    // Doing the facebooklogin
    private void facebookLogin() {
        final List<String> permissions = Arrays.asList("basic_info", "email");
        final Button facebookLogin = (Button) findViewById(R.id.facebookLoginButton);
        facebookLogin.setClickable(false);
        facebookLogin.setEnabled(false);
        progressDialog = ProgressDialog.show(this, "Logging in" , " Please wait ... ", true);
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {

                    if (parseUser == null) {
                        System.out.println("Uh oh. The user cancelled the Facebook login.");
                        if (e != null) {
                            e.printStackTrace();
                        }
                    } else {
                        User user = (User)parseUser;
                        if (user.isNew()) {
                            System.out.println("User signed up and logged in through Facebook!");
                            // Add user to channels:
                            User.refreshChannels();
                            updateUserData();

                        } else {
                            System.out.println("User logged in through Facebook!");
                            // Add user to channels:
                            User.refreshChannels();

                            startMain();
                        }
                    }
                }

                else {
                    e.printStackTrace();
                }

            }

        });
    }


    // Making the User object from the facebook-login.
    private void updateUserData() {
        final Button facebookLogin = (Button) findViewById(R.id.facebookLoginButton);
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        facebookID = user.getId();


                        // The URL for facebook profilepicture with the facebook user ID.
                        final String profile_picture_URL = "http://graph.facebook.com/" + facebookID + "/picture?type=large";
                        facebookLogin.setClickable(false);
                        facebookLogin.setEnabled(false);


                        final ParseUser currentUser = ParseUser.getCurrentUser();


                        currentUser.setEmail((String) user.getProperty("email"));
                        currentUser.setUsername((String) user.getProperty("email"));
                        currentUser.put("displayName", user.getFirstName() + " " + user.getLastName());
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    ParseUser.getCurrentUser().deleteEventually();
                                    ParseUser.logOut();
                                    System.out.println("EMAILINUSE:   " + e.getMessage());
                                    facebookLogin.setClickable(true);
                                    facebookLogin.setEnabled(true);

                                } else {
                                    // The asyncprocess for downlading the picture
                                    new FacebookProfilePictureDownloader().execute(profile_picture_URL);
                                    facebookLogin.setClickable(true);
                                    facebookLogin.setEnabled(true);
                                    startMain();
                                }

                            }
                        });
                    }
                }
        );
        request.executeAsync();

    }


    // when logged in is clicked
    private void loginuser() {

        final Button login = (Button) findViewById(R.id.buttonLogin);
        EditText email = (EditText) findViewById(R.id.editTextEmail);
        EditText password = (EditText) findViewById(R.id.editTextPassword);
        String user = email.getText().toString().toLowerCase().trim();
        String pwd = password.getText().toString();
        pgb.setVisibility(View.VISIBLE);

        if (user != null && !user.isEmpty() && pwd != null && !pwd.isEmpty()) {

            // To prevent spamming of API-calls
            login.setClickable(false);
            login.setEnabled(false);
            System.out.println("Logging in...");
            final ProgressDialog loginProgress = ProgressDialog.show(LoginActivity.this, "Logging in" , " Please wait ... ", true);
            ParseUser.logInInBackground(user, pwd, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    loginProgress.dismiss();
                    pgb.setVisibility(View.INVISIBLE);
                    if (e == null) {
                        User tmpUser = (User)user;

                        System.out.println("Login successful");
                        // Add user to channels:
                        User.refreshChannels();
                        startMain();
                    } else {
                        // Login failed
                        e.printStackTrace();
                        login.setClickable(true);
                        login.setEnabled(true);
                        Context context = getApplicationContext();
                        String text;
                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            text = "Wrong email/password. Please try again";
                        } else {
                            text = "Something went wrong. Please try again";
                        }
                        ToastMaker.makeLongToast(text,getApplicationContext());
                    }
                }
            });
        } else {
            pgb.setVisibility(View.INVISIBLE);
            ToastMaker.makeLongToast("email/password must be filled out.",this);

        }
    }
    // Start mainActivity
    private void startMain() {
        if (progressDialog != null) progressDialog.dismiss();

        Context context = this;
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
