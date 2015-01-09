package com.gjermundbjaanes.apps.roommates.notloggedin.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.FacebookProfilePictureDownloader;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

public class FacebookLogin {
    Context context;
    Button facebookButton;
    LoginActivity loginActivity;

    public FacebookLogin(Context context, Button facebookButton, LoginActivity loginActivity) {
        this.context = context;
        this.facebookButton = facebookButton;
        this.loginActivity = loginActivity;
    }

    protected void startFacebookLogin() {
        final List<String> permissions = getFacebookPermissions();

        disableFacebookButton();

        final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.logging_in), context.getString(R.string.please_wait), true);

        ParseFacebookUtils.logIn(permissions, loginActivity, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                progressDialog.dismiss();
                enableFacebookButton();

                if (e == null) {
                    if (parseUser != null && parseUser.isNew()) {
                        if (ParseFacebookUtils.getSession().isPermissionGranted("email")) {
                            User.refreshChannels();
                            updateUserData();
                        } else {
                            ToastMaker.makeLongToast("Roommates cannot create a user wihtout email permissions. Grant permission or register a user without Facebook.", context);
                            parseUser.deleteEventually();
                            User.logOut();
                        }
                    } else {
                        User.refreshChannels();
                        loginActivity.startMainActivity();
                    }
                } else {
                    ToastMaker.makeLongToast(e.getMessage(), context);
                }
            }
        });
    }

    private List<String> getFacebookPermissions() {
        return Arrays.asList("public_profile", "email");
    }

    private void disableFacebookButton() {
        facebookButton.setClickable(false);
        facebookButton.setEnabled(false);
    }

    private void enableFacebookButton() {
        facebookButton.setClickable(true);
        facebookButton.setEnabled(true);
    }

    // Making the User object from the facebook-login.
    private void updateUserData() {

        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                final ParseUser currentUser = ParseUser.getCurrentUser();

                String facebookID = user.getId();

                // The URL for facebook profilepicture with the facebook user ID.
                final String profilePictureUrl = "http://graph.facebook.com/" + facebookID + "/picture?type=large";
                facebookButton.setClickable(false);
                facebookButton.setEnabled(false);

                currentUser.setEmail((String) user.getProperty("email"));
                currentUser.setUsername((String) user.getProperty("email"));
                currentUser.put("displayName", user.getFirstName() + " " + user.getLastName());
                currentUser.saveInBackground(new UserSaveCallback(profilePictureUrl));
            }
        });

        Request.executeBatchAsync(request);

    }

    private class UserSaveCallback extends SaveCallback {
        private final String profilePictureURL;

        public UserSaveCallback(String profilePictureURL) {
            this.profilePictureURL = profilePictureURL;
        }

        @Override
        public void done(ParseException parseException) {
            enableFacebookButton();
            if (parseException == null) {
                new FacebookProfilePictureDownloader().execute(profilePictureURL);
                loginActivity.startMainActivity();
            } else {
                // Something went wrong, bail out.
                ParseUser.getCurrentUser().deleteEventually();
                ParseUser.logOut();
            }

        }
    }
}
