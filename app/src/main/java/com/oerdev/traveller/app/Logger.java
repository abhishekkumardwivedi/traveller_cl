package com.oerdev.traveller.app;


import com.oerdev.traveller.BuildConfig;

import android.util.Log;

/**
 * Created by abhishek on 17/8/16.
 */
public class Logger {

    public static void d(String TAG, String arg) {

        if(BuildConfig.DEBUG) {
            Log.d(TAG, arg);
        }
    }
}
