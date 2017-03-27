package com.oerdev.traveller.Google;

import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.R;

import android.content.Context;

/**
 * Created by abhishek on 2/10/16.
 */

public class GooglePlaceGetter {
    private static final String TAG = GooglePlaceGetter.class.getSimpleName();

    private static final String URL_GOOGLE_PLACE_DETAILS_BASE = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String URL_GOOGLE_PLACE_IMAGE_BASE = "";

    private int mWidth;
    private int mHeight;

    private Context mContext;

    public GooglePlaceGetter(Context context) {
        mContext = context;
    }

    public void setImageSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void getGooglePlaceDetails(String placeId) {
        String googlePlaceUrl = URL_GOOGLE_PLACE_DETAILS_BASE +
                "placeid=" + placeId +
                "&key=" + mContext.getResources().getString(R.string.google_api_key);

        HttpHandler httpHandler = new HttpHandler(mContext);
        httpHandler.getGooglePlaceDetailsJson(googlePlaceUrl);
    }
}
