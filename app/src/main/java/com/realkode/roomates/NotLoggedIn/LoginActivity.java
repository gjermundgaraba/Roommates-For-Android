package com.realkode.roomates.NotLoggedIn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.*;
import com.realkode.roomates.Helpers.ButtonOnTouchListener;
import com.realkode.roomates.Helpers.FacebookProfilePictureDownloader;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.MainActivity;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.Arrays;
import java.util.List;


@SuppressLint("DefaultLocale")
public class LoginActivity extends Activity {

    private ProgressBar progressBar;
    private String facebookID;
    private ProgressDialog progressDialog;

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

    private void startMainActivity() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

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
                        startSignupActivity();
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
                        facebookLogin();
                    }
                }));

        resetPassword.setOnTouchListener(new ButtonOnTouchListener(new ButtonOnTouchListener.TouchActionHandler() {
                    @Override
                    public void performAction() {
                        forgotPassword();
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar_Login);
    }

    // The result for facebookloginactivity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    private void forgotPassword() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder.setTitle(getString(R.string.forgot_password))
                .setMessage(getString(R.string.enter_your_email)).setCancelable(false).setView(promptsView)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String email = userInput.getText().toString().toLowerCase().trim();
                        final ProgressDialog resetProgress = ProgressDialog
                                .show(LoginActivity.this, "Resetting Password", " Please wait ... ", true);
                        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                    public void done(ParseException e) {
                                        resetProgress.dismiss();
                                        if (e == null) {
                                            ToastMaker.makeLongToast(R.string.reset_email_sent_with_instructions,
                                                    getApplicationContext());
                                        } else {
                                            ToastMaker.makeLongToast(e.getMessage(), getApplicationContext());
                                        }
                                    }
                                });

                    }
                }).setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

    }

    private void startSignupActivity() {
        Context context = this;
        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
    }

    private void facebookLogin() {
        final List<String> permissions = Arrays.asList("basic_info", "email");
        final Button facebookLogin = (Button) findViewById(R.id.facebookLoginButton);
        facebookLogin.setClickable(false);
        facebookLogin.setEnabled(false);
        progressDialog = ProgressDialog.show(this, "Logging in", " Please wait ... ", true);
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null && parseUser != null) {
                    User user = (User) parseUser;
                    if (user.isNew()) {
                        User.refreshChannels();
                        updateUserData();
                    } else {
                        // Facebook login
                        User.refreshChannels();
                        startMainActivity();
                    }
                } else {
                    ToastMaker.makeLongToast(getString(R.string.something_went_wrong), LoginActivity.this);
                }
            }
        });
    }

    // Making the User object from the facebook-login.
    private void updateUserData() {
        final Button facebookLogin = (Button) findViewById(R.id.facebookLoginButton);
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        facebookID = user.getId();

                        // The URL for facebook profilepicture with the facebook user ID.
                        final String profile_picture_URL =
                                "http://graph.facebook.com/" + facebookID + "/picture?type=large";
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
                                    startMainActivity();
                                }

                            }
                        });
                    }
                });
        request.executeAsync();

    }

    private void loginUser() {
        final Button login = (Button) findViewById(R.id.buttonLogin);
        EditText email = (EditText) findViewById(R.id.editTextEmail);
        EditText password = (EditText) findViewById(R.id.editTextPassword);
        String user = email.getText().toString().toLowerCase().trim();
        String pwd = password.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        if (!user.isEmpty() && pwd != null && !pwd.isEmpty()) {
            login.setClickable(false);
            login.setEnabled(false);
            final ProgressDialog loginProgress =
                    ProgressDialog.show(LoginActivity.this, "Logging in", " Please wait ... ", true);
            ParseUser.logInInBackground(user, pwd, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    loginProgress.dismiss();
                    progressBar.setVisibility(View.INVISIBLE);
                    if (e == null) {
                        User.refreshChannels();
                        startMainActivity();
                    } else {
                        login.setClickable(true);
                        login.setEnabled(true);

                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            ToastMaker.makeLongToast("Wrong email/password. Please try again", getApplicationContext());
                        } else {
                            ToastMaker.makeLongToast("Something went wrong. Please try again", getApplicationContext());
                        }

                    }
                }
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            ToastMaker.makeLongToast("email/password must be filled out.", this);
        }
    }
}
