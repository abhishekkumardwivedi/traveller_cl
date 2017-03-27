package com.oerdev.traveller.myPlan;

import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.ItemsModel.Plan;
import com.oerdev.traveller.R;

import android.content.Context;
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

public class MyPlanFragment extends Fragment {

    public static final List<Plan> PLANS_ITEMS = new ArrayList<Plan>();
    public static final Map<String, Plan> PLACE_PLAN_MAP = new HashMap<String, Plan>();
    //    public static final Map<String, Place> PLAN_PLACE_MAP = new HashMap<String, Place>();
    private static final String TAG = MyPlanFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static MyPlanRecyclerViewAdapter mAdapter;
    private int mColumnCount = 1;
    private MyPlanFIListener mListener;
    private Context mContext;

    public MyPlanFragment() {
    }

    public static void logout() {
        PLACE_PLAN_MAP.clear();
        PLANS_ITEMS.clear();
        PLACE_PLAN_MAP.clear();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myplan_fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyPlanRecyclerViewAdapter(this.getContext(),
                    PLANS_ITEMS, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyPlanFIListener) {
            mListener = (MyPlanFIListener) context;
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
    }

    public interface MyPlanFIListener {
        void onPlaceClick(Place place, int tappedItem);
    }
}
