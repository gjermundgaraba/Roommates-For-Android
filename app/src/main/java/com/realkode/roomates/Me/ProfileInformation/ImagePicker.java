package com.realkode.roomates.Me.ProfileInformation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;

class ImagePicker {
    public static final int IMAGE_PICKER_SELECT = 0;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    private final Activity activity;
    private final Context context;

    ImagePicker(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }

    public void pickImage() {
        showImagePickerDialog();
    }

    // Dialog to choose between camera and gallery image
    private void showImagePickerDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
        myAlertDialog.setTitle("Profile Picture");
        myAlertDialog.setMessage("Select Picture Source");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                getImageFromGallery();
            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                getImageFromCamera();
            }
        });
        myAlertDialog.show();
    }

    private void getImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, IMAGE_PICKER_SELECT);
    }
}
