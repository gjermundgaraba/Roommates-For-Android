package com.realkode.roomates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * This class is for downloading the profile picture from facebook asynchronously
 * from the other work the application is doing.
 */

public class FacebookProfilePictureDownloader extends AsyncTask<String, Integer, Bitmap> {


    // The callback when the asynctask is done.
    @Override
    protected void onPostExecute(Bitmap result) {

        if (result != null) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Saving the image in the PNG-format.
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Saving the image as "objectID".png
                ParseFile image = new ParseFile(currentUser.getObjectId() + ".png", byteArray);

                try {
                    image.save();
                    currentUser.put("profilePicture", image);
                    currentUser.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        return;

    }

    // The method with the task that shall be executed in background
    @Override
    protected Bitmap doInBackground(String... strings) {

        Bitmap bmp = getBitmapFromURL(strings[0]);

        return bmp;
    }
    // The method called for downloading the picture. Takes an URL as parameter.
    public Bitmap getBitmapFromURL(String src) {
        Bitmap bitmap = null;
        try {

            HttpURLConnection con = (HttpURLConnection) new URL(src).openConnection();
            con.setInstanceFollowRedirects(false);
            con.connect();
            // The link to the facebookpicture will redirect, and we need the second URL to fetch the picture
            String realURL = con.getHeaderField("Location").toString();  // Get the redirected URL

            URL url = new URL(realURL);

            InputStream in = url.openStream();

            bitmap = BitmapFactory.decodeStream(in);

            return bitmap;
        } catch (Exception e) {
            Log.e("ImageDownlaod", e.getMessage());
        }
        return bitmap;
    }

}