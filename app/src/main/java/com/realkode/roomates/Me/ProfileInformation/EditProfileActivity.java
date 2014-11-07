package com.realkode.roomates.Me.ProfileInformation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.BitmapUtils;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class EditProfileActivity extends Activity {
    private ParseImageView profilePictureImageView;
    private ImagePicker imagePicker;
    private Bitmap newProfilePicture;

    EditText displayNameField;
    EditText emailField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile);

        imagePicker = new ImagePicker(this);

        setUpUI();
    }

    private void setUpUI() {
        User user = User.getCurrentUser();

        displayNameField = (EditText) findViewById(R.id.changeDisplayName);
        displayNameField.setText(user.getDisplayName());

        emailField = (EditText) findViewById(R.id.changeEmail);
        emailField.setText(user.getEmail());

        Button changePasswordButton = (Button) findViewById(R.id.buttonChangePassword);
        changePasswordButton.setOnClickListener(new ChangePasswordOnClickListener());

        Button updateButton = (Button) findViewById(R.id.buttonUpdateProfile);
        updateButton.setOnClickListener(new UpdateProfileOnClickListener());

        profilePictureImageView = (ParseImageView) findViewById(R.id.imageViewChangePicture);
        profilePictureImageView.setOnClickListener(new ProfilePicturePickerOnClickListener());
        profilePictureImageView.setPlaceholder(getResources().getDrawable(R.drawable.placeholder));

        if (user.getProfilePicture() != null) {
            profilePictureImageView.setParseFile(user.getProfilePicture());
            profilePictureImageView.loadInBackground();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImagePicker.IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            newProfilePicture = BitmapUtils.getBitmapFromGalleryData(data, this);
            profilePictureImageView.setImageBitmap(newProfilePicture);
        } else if (requestCode == ImagePicker.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = BitmapUtils.cropBitmap(imageBitmap);
            newProfilePicture = imageBitmap;
            profilePictureImageView.setImageBitmap(imageBitmap);
        }
    }

    private class ChangePasswordOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, ChangePasswordActivity.class);
            startActivity(intent);
        }
    }

    private class ProfilePicturePickerOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            imagePicker.pickImage();
        }
    }

    private class UpdateProfileOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String displayName = displayNameField.getText().toString();
            String email = emailField.getText().toString();
            User user = User.getCurrentUser();

            final Context context = EditProfileActivity.this;
            final ProgressDialog progressDialog = showSaveProgressDialog(context);
            SaveProfileInformation saveProfileInformation = new SaveProfileInformation(displayName, email, newProfilePicture, user);

            try {
                saveProfileInformation.performSave(new SaveProfileCallback(progressDialog, context));
            } catch (ParseException e) {
                ToastMaker.makeLongToast(e.getMessage(), context);
            }
        }
    }

    private ProgressDialog showSaveProgressDialog(Context context) {
        return ProgressDialog.show(context,context.getString(R.string.changing_profile_information), context.getString(R.string.please_wait), true);
    }

    private class SaveProfileCallback extends SaveCallback {
        private final ProgressDialog progressDialog;
        private final Context context;

        public SaveProfileCallback(ProgressDialog progressDialog, Context context) {
            this.progressDialog = progressDialog;
            this.context = context;
        }

        @Override
        public void done(ParseException e) {
            progressDialog.dismiss();
            if (e == null) {
                ToastMaker.makeLongToast(R.string.profile_updated, context);
            } else {
                ToastMaker.makeLongToast(e.getMessage(), context);
            }

            EditProfileActivity.this.finish();

        }
    }
}

