package com.oerdev.traveller.PlaceDetails;

import com.bumptech.glide.Glide;
import com.oerdev.traveller.Google.MapsActivity;
import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.Logger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by abhishek on 16/9/16.
 */

public class DetailsFragment extends Fragment {

    private static final String TAG = DetailsFragment.class.getSimpleName();

    private static final int width = 300;
    private static final int height = 200;
    private static final String urlWiki = "https://en.wikipedia.org/wiki/";
    int mZoom;
    private TextView detailText;
    private ImageView latlngImageView;
    private TextView wiki;
    private Place mPlace;
    private static final int [] mZoomSeek = {4, 5, 7, 9, 11, 13, 15};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPlace = ((DetailedActivity) getActivity()).getPlace();
        mZoom = 12;
        return inflater.inflate(R.layout.fragment_details_activity_details_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailText = (TextView) view.findViewById(R.id.place_detail);
        detailText.setText(mPlace.details);

        latlngImageView = (ImageView) view.findViewById(R.id.place_map);

        zoomMapLoad(mZoomSeek[5]);

        wiki = (TextView) view.findViewById(R.id.place_wiki);
        wiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(urlWiki + mPlace.name));
                startActivity(intent);
            }
        });

        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                zoomMapLoad(mZoomSeek[i]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void zoomMapLoad(int zoom) {
        String map = "https://maps.googleapis.com/maps/api/staticmap?center="
                + mPlace.latitude + "," + mPlace.longitude
                + "&zoom=" + zoom
                + "&size=" + width + "x" + height;
        try {
            String marker_me = "color:orange|label:1|" + mPlace.name;
            marker_me = URLEncoder.encode(marker_me, "UTF-8");
            map = map + "&markers=" + marker_me;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logger.d(TAG, map);
        Glide.with(this).load(map).into(latlngImageView);
        latlngImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("from", DetailsFragment.class.getSimpleName());
                intent.putExtra("place", mPlace);
                startActivity(intent);
            }
        });
    }
}
