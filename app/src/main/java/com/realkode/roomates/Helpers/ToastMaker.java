package com.realkode.roomates.Helpers;

import android.content.Context;
import android.widget.Toast;

/**
 * Helper class to display toast messages.
 */
public class ToastMaker {
    public static void makeShortToast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }
}
