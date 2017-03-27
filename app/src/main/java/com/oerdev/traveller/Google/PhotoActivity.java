package com.oerdev.traveller.Google;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import com.oerdev.traveller.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoActivity extends FragmentActivity {
    private static final String TAG = PhotoActivity.class.getSimpleName();
    Context mContext;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mContext = getApplicationContext();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        placePhotosTask();
    }


    private void placePhotosTask() {

        final String placeId = "ChIJ2UEvfIUNdDkRQjtSqTjvSng"; // Australian Cruise Group
        final ImageView mImageView = (ImageView) findViewById(R.id.google_image);
        final TextView mText = (TextView) findViewById(R.id.google_text);

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(mImageView.getWidth(), mImageView.getHeight()) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                mImageView.setImageResource(R.drawable.landscape);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    mImageView.setImageBitmap(attributedPhoto.bitmap);

                    // Display the attribution as HTML content if set.
                    if (attributedPhoto.attribution == null) {
                        mText.setVisibility(View.GONE);
                    } else {
                        mText.setVisibility(View.VISIBLE);
                        mText.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                    }

                }
            }
        }.execute(placeId);
    }

    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> implements GoogleApiClient.OnConnectionFailedListener {

        private int mHeight;
        private int mWidth;
        private AttributedPhoto attributedPhoto = null;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         * The place id must be the first (and only) parameter.
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];

            //////////////////////
            Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                @Override
                public void onResult(PlacePhotoMetadataResult placePhotoMetadataResult) {
                    if (placePhotoMetadataResult.getStatus().isSuccess()) {
                        PlacePhotoMetadataBuffer photoMetadata = placePhotoMetadataResult.getPhotoMetadata();
                        int photoCount = photoMetadata.getCount();
                        if (photoCount > 0) {
                            PlacePhotoMetadata placePhotoMetadata = photoMetadata.get(0);
                            CharSequence attribution = placePhotoMetadata.getAttributions();
                            Bitmap image = placePhotoMetadata.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                                    .getBitmap();
                            attributedPhoto = new AttributedPhoto(attribution, image);
//                            final String photoDetail = placePhotoMetadata.toString();
//                            placePhotoMetadata.getScaledPhoto(mGoogleApiClient, 500, 500).setResultCallback(new ResultCallback<PlacePhotoResult>() {
//                                @Override
//                                public void onResult(PlacePhotoResult placePhotoResult) {
//                                    if (placePhotoResult.getStatus().isSuccess()) {
//                                        Log.i(TAG, "Photo "+photoDetail+" loaded");
//                                    } else {
//                                        Log.e(TAG, "Photo "+photoDetail+" failed to load");
//                                    }
//                                }
//                            });
//                        }
//                        photoMetadata.release();
                        }
                    }
                }
            });
            //////////////////////

//            PlacePhotoMetadataResult result = Places.GeoDataApi
//                    .getPlacePhotos(mGoogleApiClient, placeId).await();
//
//            if (result.getStatus().isSuccess()) {
//                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
//                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
//                    // Get the first bitmap and its attributions.
//                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
//                    CharSequence attribution = photo.getAttributions();
//                    // Load a scaled bitmap for this photo.
//                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
//                            .getBitmap();
//
//                    attributedPhoto = new AttributedPhoto(attribution, image);
//                }
//                // Release the PlacePhotoMetadataBuffer.
//                photoMetadataBuffer.release();
//            }
            return attributedPhoto;
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        /**
         * Holder for an image and its attribution.
         */
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }
}
