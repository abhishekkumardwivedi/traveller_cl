package com.oerdev.traveller.Suggest;

import com.oerdev.traveller.Google.LocationService;
import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.Logger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestFragment extends Fragment {

    private static final String TAG = SuggestFragment.class.getSimpleName();
    public static final List<Place> SUGGEST_ITEMS = new ArrayList<Place>();
    public static final Map<String, Place> SUGGEST_ITEM_MAP = new HashMap<String, Place>();
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static SuggestRecyclerViewAdapter mAdapter;
    private int mColumnCount = 2;
    private SuggestFIListener mListener;

    public SuggestFragment() {
    }

    public static SuggestFragment getInstance() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        LocationService locationService = new LocationService(getContext(), getActivity());
        locationService.updateLocation();
    }

    public static void logout() {
        SUGGEST_ITEMS.clear();
        SUGGEST_ITEM_MAP.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.suggest_fragment_item_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

            mAdapter = new SuggestRecyclerViewAdapter(this.getContext(),
                    SUGGEST_ITEMS, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.d(TAG, "onAttach");
        if (context instanceof SuggestFIListener) {
            mListener = (SuggestFIListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SuggestFIListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d(TAG, "onDetach");
        mListener = null;
    }

    public interface SuggestFIListener {
        void onPlaceClick(Place place, int tappedItem);
    }
}
