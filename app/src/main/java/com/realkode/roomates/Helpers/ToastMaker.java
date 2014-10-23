package com.realkode.roomates.Helpers;

import android.content.Context;
import android.widget.Toast;

public class ToastMaker {
    public static void makeShortToast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public static void makeShortToast(int resourceId, Context context) {
        Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }

    public static void makeLongToast(int resourceId, Context context) {
        Toast.makeText(context, resourceId, Toast.LENGTH_LONG).show();
    }
}
