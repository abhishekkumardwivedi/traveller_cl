package com.oerdev.traveller.PlaceDetails;

import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by abhishek on 17/9/16.
 */

public class BookingsFragment extends Fragment {

    Place mPlace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPlace = ((DetailedActivity)getActivity()).getPlace();
        return inflater.inflate(R.layout.fragment_details_activity_details_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
