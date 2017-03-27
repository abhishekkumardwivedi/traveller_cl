package com.oerdev.traveller.Hangout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.Logger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class HangoutFragment extends Fragment{

    public static final List<Place> HANGOUT_ITEMS = new ArrayList<Place>();
    public static final Map<String, Place> HANGOUT_ITEM_MAP = new HashMap<String, Place>();
    public static final List<Place> HANGOUT_NEW_ITEMS = new ArrayList<Place>();
    public static final Map<String, Place> HANGOUT_NEW_ITEM_MAP = new HashMap<String, Place>();

    private static final String TAG = HangoutFragment.class.getSimpleName();
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static HangoutRecyclerViewAdapter mAdapter;
    private static GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 1;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private PopularFIListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HangoutFragment() {

    }

    public static void logout() {
        HANGOUT_NEW_ITEM_MAP.clear();
        HANGOUT_NEW_ITEMS.clear();
        HANGOUT_ITEM_MAP.clear();
        HANGOUT_ITEMS.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        HttpHandler httpHandler = new HttpHandler();
//        httpHandler.getHangoutContent();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hangout_fragment_item_list, container, false);

        View view = rootView.findViewById(R.id.hangout_recycler_view);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new HangoutRecyclerViewAdapter(this.getContext(), HANGOUT_NEW_ITEMS,
                    HANGOUT_ITEMS, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PopularFIListener) {
            mListener = (PopularFIListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PopularFIListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        View newHangoutCardView = view.findViewById(R.id.start_hangout_card_view);
//        View newHangout = view.findViewById(R.id.start_hangout);
//        newHangoutCardView.setOnClickListener(this);
//        newHangout.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.start_hangout:
//            case R.id.start_hangout_card_view:
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
//                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//                break;
//        }
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place p
                        = PlacePicker.getPlace(getContext(), data);
                Logger.d(TAG, "Name :" + p.getName());
                Logger.d(TAG, "Addr :" + p.getAddress());
                Logger.d(TAG, "Id   :" + p.getId());

                Place place = new Place(p.getId(), p.getName().toString(), p.getLatLng().latitude, p.getLatLng().longitude, null);
                HttpHandler httpHandler = new HttpHandler(getContext());
                httpHandler.newHangout(getContext(), place);
            }
        }
    }

    public interface PopularFIListener {
        void onPlaceClick(Place place, int tappedItem);
    }
}
