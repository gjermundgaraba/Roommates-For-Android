package com.realkode.roomates.Me;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.InputValidation;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Activity to edit the user profile
 */
public class EditProfileActivity extends Activity {
    private static final int IMAGE_PICKER_SELECT = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ParseImageView profilePictureImageView;
    private static Bitmap newPic;
    private String mCurrentPhotoPath;


    // Called when creating activity
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final User user = User.getCurrentUser();

        //get UI-elements
        final EditText displayNameEditText = (EditText) findViewById(R.id.changeDisplayName);
        final EditText emailEditText = (EditText) findViewById(R.id.changeEmail);
        Button changePasswordButton = (Button) findViewById(R.id.buttonChangePassword);
        Button updateButton = (Button) findViewById(R.id.buttonUpdateProfile);
        profilePictureImageView = (ParseImageView) findViewById(R.id.imageViewChangePicture);

        if (user.getProfilePicture() != null) {

            profilePictureImageView.setParseFile(user.getProfilePicture());
            profilePictureImageView.loadInBackground();
        }
        //set up UI


        displayNameEditText.setText(user.getDisplayName());
        emailEditText.setText(user.getEmail());

        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String display_name = displayNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                if (display_name.isEmpty() || email.isEmpty()) {
                    ToastMaker.makeLongToast("All fields must be filled out",getApplicationContext());
                } else if (InputValidation.emailIsValid(email)) {
                    user.setUsername(email);
                    user.setEmail(email);
                    user.setDisplayName(display_name);
                    ParseFile parsePicture = new ParseFile("null".getBytes());
                    if (newPic != null) {
                        parsePicture = new ParseFile(user.getObjectId() + ".png", bitmapToByteArray(newPic));
                        if (!parsePicture.equals(user.getProfilePicture())) {
                            System.out.println("CHANGED PICTURE");
                            user.setProfilePicture(parsePicture);
                        }
                    }


                    final ProgressDialog changeProgress = ProgressDialog.show(EditProfileActivity.this, "Changing Profile Information", " Please wait ... ", true);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            changeProgress.dismiss();
                            if (e == null)
                                ToastMaker.makeLongToast("Profile Updated",getApplicationContext());
                            else
                                ToastMaker.makeLongToast(e.getMessage(),getApplicationContext());
                            EditProfileActivity.this.finish();
                        }
                    });
                }


            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Image_Picker_Dialog();
            }
        });
    }

    // Dialog to choose between camera and gallery image
    public void Image_Picker_Dialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
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

    /**
     * Photo Selection result
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {


            Bitmap bitmap = getBitmapFromGalleryData(data, this);

            profilePictureImageView.setImageBitmap(bitmap);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = cropBitmap(imageBitmap);
            newPic = imageBitmap;
            profilePictureImageView.setImageBitmap(imageBitmap);
        }
    }

    /**
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * checking how big version of the picture we need to load to display the thumbnail effectively
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 3;
            final int halfWidth = width / 3;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getBitmapFromGalleryData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        System.out.println("PICTURE PATH:" + picturePath);
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 500, 500);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;


        // getting the bitmap after we know the samplesize
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
        try {

            // Correction rotation of picture
            ExifInterface exif = new ExifInterface(picturePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Scaling and cropping the bitmap for our usage
        bitmap = Bitmap.createScaledBitmap(cropBitmap(bitmap), 200, 200, false);
        newPic = bitmap;
        return bitmap;
    }

    // Cropping the bitmap so it is square.
    private static Bitmap cropBitmap(Bitmap bitmap) {
        Bitmap dstBmp;
        if (bitmap.getWidth() >= bitmap.getHeight()) {

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        return dstBmp;
    }

    public void getImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void getImageFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_PICKER_SELECT);
    }

    public static byte[] bitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}

