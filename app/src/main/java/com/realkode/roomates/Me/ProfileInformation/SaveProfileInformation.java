package com.realkode.roomates.Me.ProfileInformation;

import android.graphics.Bitmap;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.realkode.roomates.App;
import com.realkode.roomates.Helpers.BitmapUtils;
import com.realkode.roomates.Helpers.InputValidation;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class SaveProfileInformation {
    String displayName;
    String email;
    Bitmap profilePicture;
    User user;

    public SaveProfileInformation(String displayName, String email, Bitmap profilePicture, User user) {
        this.displayName = displayName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.user = user;
    }

    public void performSave(SaveCallback saveCallback) throws ParseException {

        if (displayName.isEmpty() || email.isEmpty()) {
            error(R.string.all_field_must_be_filled_out);
        } else if (!InputValidation.emailIsValid(email)) {
            error(R.string.email_is_not_valid);
        } else {
            user.setUsername(email);
            user.setEmail(email);
            user.setDisplayName(displayName);
            setProfilePicture();

            user.saveInBackground(saveCallback);
        }
    }

    private void setProfilePicture() {
        if (profilePicture != null) {
            ParseFile parsePicture = new ParseFile(user.getObjectId() + ".png", BitmapUtils.bitmapToByteArray(profilePicture));
            if (!parsePicture.equals(user.getProfilePicture())) {
                user.setProfilePicture(parsePicture);
            }
        }
    }

    private void error(int error) throws ParseException {
        throw new ParseException(App.getContext().getString(error), null);
    }
}
