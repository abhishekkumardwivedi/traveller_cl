package com.oerdev.traveller.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by abhishek on 21/9/16.
 */

public class PermissionHandler {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Context mContext;
    Activity mActivity;

    public PermissionHandler(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    public boolean checkSelfPermission(String permission) {
        return false;
    }

    public boolean getPermissionGranted(String permission) {

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        return false;
    }
}
