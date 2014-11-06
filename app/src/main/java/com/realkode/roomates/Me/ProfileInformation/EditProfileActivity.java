package com.realkode.roomates.Me.ProfileInformation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseImageView;
import com.realkode.roomates.Helpers.BitmapUtils;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;


/**
 * Activity to edit the user profile
 */
public class EditProfileActivity extends Activity {
    private Bitmap newPic;
    private ParseImageView profilePictureImageView;
    private ImagePicker imagePicker;

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

        EditText displayNameEditText = (EditText) findViewById(R.id.changeDisplayName);
        displayNameEditText.setText(user.getDisplayName());

        EditText emailEditText = (EditText) findViewById(R.id.changeEmail);
        emailEditText.setText(user.getEmail());

        Button changePasswordButton = (Button) findViewById(R.id.buttonChangePassword);
        changePasswordButton.setOnClickListener(new ChangePasswordOnClickListener());

        Button updateButton = (Button) findViewById(R.id.buttonUpdateProfile);
        updateButton
                .setOnClickListener(new UpdateProfileOnClickListener(displayNameEditText, emailEditText, newPic, this));

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
            newPic = BitmapUtils.getBitmapFromGalleryData(data, this);
            profilePictureImageView.setImageBitmap(newPic);
        } else if (requestCode == ImagePicker.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = BitmapUtils.cropBitmap(imageBitmap);
            newPic = imageBitmap;
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

}

