package com.realkode.roomates.Helpers;

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
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

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
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return getBitmapFromURL(strings[0]);
    }

    Bitmap getBitmapFromURL(String urlString) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
            con.setInstanceFollowRedirects(false);
            con.connect();

            // The link to the facebookpicture will redirect, and we need the second URL to fetch the picture
            String realURL = con.getHeaderField("Location");  // Get the redirected URL
            URL url = new URL(realURL);
            InputStream in = url.openStream();

            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ImageDownlaod", e.getMessage());
        }

        return null;
    }

}