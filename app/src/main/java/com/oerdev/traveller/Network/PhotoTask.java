package com.oerdev.traveller.Network;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import com.oerdev.traveller.app.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by abhishek on 30/9/16.
 */

public abstract class PhotoTask extends AsyncTask<String, Void, Bitmap>
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = PhotoTask.class.getSimpleName();

    private Context mContext;
    private int mHeight;
    private int mWidth;

    public PhotoTask(Context context, int height, int width) {
        mContext = context;
        mHeight = height;
        mWidth = width;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap image = null;

        if (params.length != 1) {
            return null;
        }
        final String placeId = params[0];

        GoogleApiClient mGoogleApiClient =
                new GoogleApiClient
                        .Builder(mContext)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

        mGoogleApiClient.connect();

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                // Get the first bitmap and its attributions.
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                CharSequence attribution = photo.getAttributions();
                // Load a scaled bitmap for this photo.
                image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                        .getBitmap();
            }
            photoMetadataBuffer.release();
        }
        return image;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d(TAG, "connectionFailed: " + connectionResult.getErrorMessage());
    }
}

