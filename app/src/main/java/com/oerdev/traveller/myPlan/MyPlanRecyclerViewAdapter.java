package com.oerdev.traveller.myPlan;

import com.bumptech.glide.Glide;
import com.oerdev.traveller.ItemsModel.Plan;
import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.AppConfigs;
import com.oerdev.traveller.app.Logger;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyPlanRecyclerViewAdapter extends RecyclerView.Adapter<MyPlanRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = MyPlanRecyclerViewAdapter.class.getSimpleName();

    private static final int CALENDAR_FRAGMENT = 1;
    private final List<Plan> mValues;
    private final MyPlanFragment.MyPlanFIListener mListener;
    private Context mContext;

    public MyPlanRecyclerViewAdapter(Context context, List<Plan> items,
                                     MyPlanFragment.MyPlanFIListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
        Logger.d(TAG, "MyPlanRecyclerViewAdapter()");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myplan_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        if (MyPlanFragment.PLANS_ITEMS.get(position).mPlace != null) {
            holder.mNameTextView.setText(mValues.get(position).mPlace.name);
            String imageUrl = mValues.get(position).mPlace.primaryImageUrl;

            if (imageUrl != null) {
                Glide.with(mContext).load(imageUrl).into(holder.mThumbnail);
            } else {
                ArrayList<String> gphotos = mValues.get(position).mPlace.photoReferenceList;
                if (gphotos != null && gphotos.size() > 0) {
                    String url = "https://maps.googleapis.com/maps/api/place/photo?"
                            + "maxwidth=300"
                            + "&photoreference=" + gphotos.get(0)
                            + "&key=" + mContext.getResources().getString(R.string.google_api_key);
                    Glide.with(mContext).load(url).into(holder.mThumbnail);
                }
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, ''yy");
        String dateMs = mValues.get(position).mDate;
        Logger.d(TAG, "date:" + dateMs);
        if (dateMs != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis((long) Double.parseDouble(dateMs));
            String myDate = format.format(c.getTime());
            holder.mDateTextView.setText(myDate);
            holder.mDateTextView.setTypeface(null, Typeface.BOLD);
        } else {
            holder.mDateTextView.setText("Add date");
            holder.mDateTextView.setTypeface(null, Typeface.ITALIC);
        }

        if (mValues.get(position).mRoutesList != null && mValues.get(position).mRoutesList.size() > 0) {
            String startLocation = mValues.get(position).mRoutesList.get(0).address;
            holder.mLocationTextView.setText(startLocation);
            holder.mLocationTextView.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.mLocationTextView.setText("add start location");
            holder.mLocationTextView.setTypeface(null, Typeface.ITALIC);
        }

        if (mValues.get(position).mFriendsList != null && mValues.get(position).mFriendsList.size() > 0) {
            for (int i = 0; i < mValues.get(position).mFriendsList.size(); i++) {
//            holder.mLocationTextView.setText(startLocation);
                TextView t = new TextView(mContext);
                t.setText(mValues.get(position).mFriendsList.get(i).name);
                t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                holder.mFriendsLinearLayout.addView(t);
            }
        } else {
            TextView t = new TextView(mContext);
            t.setText("invite friends to join or follow you in this trip");
            t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            t.setTypeface(null, Typeface.ITALIC);
            holder.mFriendsLinearLayout.addView(t);
        }

        holder.mView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        Logger.d(TAG, "item count: " + mValues.size());
        return mValues.size();
    }

    @Override
    public void onClick(View view) {

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private int mPosition;

        public void setPosition(int position) {
            mPosition = position;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            Logger.d(TAG, "Current date: " + day + "/" + month + "/" + year);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @TargetApi(Build.VERSION_CODES.N)
        public void onDateSet(DatePicker view, int year, int month, int day) {

            String date = day + "/" + month + "/" + year;
            Logger.d(TAG, "Selected date: " + date);

            final Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            c.getTimeInMillis();
            Logger.d(TAG, "In ms: " + c.getTimeInMillis());

            /////////////
            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, ''yy");
            String dateMs = Double.toString(c.getTimeInMillis());
            Calendar a = Calendar.getInstance();
            a.setTimeInMillis((long) Double.parseDouble(dateMs));
            String myDate = format.format(a.getTime());
            Logger.d(TAG, "decoded date: " + myDate);
            /////////////
            HttpHandler httpHandler = new HttpHandler();
            httpHandler.updateMyPlan();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        //     public final TextView mIdView;
        public final TextView mNameTextView;
        public final ImageView mThumbnail;
        public final ImageView mAddView;
        public final ImageView mMapView;
        public final ImageView calendarIcon;
        public final TextView mDateTextView;
        public final TextView mLocationTextView;
        public final LinearLayout mFriendsLinearLayout;

        public Plan mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //      mIdView = (TextView) view.findViewById(R.id.id);
            mNameTextView = (TextView) view.findViewById(R.id.name);
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            mAddView = (ImageView) view.findViewById(R.id.calendar_launch);
            mMapView = (ImageView) view.findViewById(R.id.map_launch);
            calendarIcon = (ImageView) view.findViewById(R.id.calendar_launch);
            mDateTextView = (TextView) view.findViewById(R.id.date);
            mLocationTextView = (TextView) view.findViewById(R.id.locations);
            mFriendsLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_friends);

            mView.setOnClickListener(this);
            mNameTextView.setOnClickListener(this);
            mThumbnail.setOnClickListener(this);
            mNameTextView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameTextView.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            mListener.onPlaceClick(mItem.mPlace, AppConfigs.ACTION_VIEW);
        }
    }
}
