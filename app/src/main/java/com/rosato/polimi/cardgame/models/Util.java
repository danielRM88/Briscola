package com.rosato.polimi.cardgame.models;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;

/**
 * Created by danielrosato on 1/4/18.
 */

public class Util {
    /**
     * Method that locks the screen orientation of the device
     *
     * @param activity
     */
    public static void lockOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        // Rotation 0 is PORTRAIT
        // Rotation 1 is LANDSCAPE LEFT
        // Roration 2 is REVERSE PORTRAIT
        // Rotation 3 is LANDSCAPE RIGHT

        if (rotation == 1) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (rotation == 3) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * Method that unlocks the screen orientation of the device
     *
     * @param activity
     */
    public static void unlockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    /**
     * Method that gets a drawable resource id from a file name
     *
     * @param name name of the file
     * @param context context of the application
     * @return resource id of the drawable
     */
    public static int getResourceId(String name, Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", context.getPackageName());

        return resourceId;
    }
}
