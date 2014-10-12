package com.realkode.roomates.Me;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.BitmapUtils;
import com.realkode.roomates.Helpers.InputValidation;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class UpdateProfileOnClickListener implements View.OnClickListener {
    private Activity activity;
    private Context context;
    private EditText displayNameEditText;
    private EditText emailEditText;
    private Bitmap newPic;
    private User user;

    UpdateProfileOnClickListener(EditText displayNameEditText, EditText emailEditText, Bitmap newPic, Activity activity) {
        this.activity = activity;
        this.context = activity;
        this.displayNameEditText = displayNameEditText;
        this.emailEditText = emailEditText;
        this.newPic = newPic;

        user = User.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        String displayName = displayNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (displayName.isEmpty() || email.isEmpty()) {
            ToastMaker.makeLongToast(R.string.toast_update_profile_missing_fields, context);
        } else if (InputValidation.emailIsValid(email)) {
            user.setUsername(email);
            user.setEmail(email);
            user.setDisplayName(displayName);
            if (newPic != null) {
                ParseFile parsePicture = new ParseFile(user.getObjectId() + ".png", BitmapUtils.bitmapToByteArray(newPic)); //TODO: IS THIS DOCUMENTED ANYWHERE?
                if (!parsePicture.equals(user.getProfilePicture())) {
                    user.setProfilePicture(parsePicture);
                }
            }

            ProgressDialog progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.toast_update_profile_saving_title),
                    context.getString(R.string.toast_update_profile_saving_message),
                    true);
            user.saveInBackground(new ProfileUpdatedSaveCallBack(progressDialog));

        } else {
            ToastMaker.makeLongToast(R.string.toast_update_profile_invalid_email, context);
        }
    }

    private class ProfileUpdatedSaveCallBack extends SaveCallback {
        private ProgressDialog progressDialog;

        ProfileUpdatedSaveCallBack(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        public void done(ParseException e) {
            progressDialog.dismiss();
            if (e == null) {
                ToastMaker.makeLongToast(R.string.toast_update_profile_success, context);
            } else {
                ToastMaker.makeLongToast(e.getMessage(), context);
            }

            activity.finish();
        }
    }
}
