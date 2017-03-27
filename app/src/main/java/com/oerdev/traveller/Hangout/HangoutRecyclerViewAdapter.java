package com.oerdev.traveller.Hangout;

import com.google.android.gms.common.api.GoogleApiClient;

import com.bumptech.glide.Glide;
import com.oerdev.traveller.Hangout.HangoutFragment.PopularFIListener;
import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.AppConfigs;
import com.oerdev.traveller.app.Logger;
import com.oerdev.traveller.app.SessionManager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HangoutRecyclerViewAdapter extends RecyclerView.Adapter<HangoutRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = HangoutRecyclerViewAdapter.class.getSimpleName();

    private final List<Place> mNewValues;
    private final List<Place> mValues;
    private final PopularFIListener mListener;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private ViewHolder testViewHolder;
    private final String myUserId;

    public HangoutRecyclerViewAdapter(Context context, List<Place> newItems, List<Place> items, PopularFIListener listener) {
        mNewValues = newItems;
        mValues = items;
        mListener = listener;
        mContext = context;
        SessionManager sessionManager = new SessionManager(mContext);
        myUserId = sessionManager.getUserId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hangout_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position < mNewValues.size()) {
            holder.mItem = mNewValues.get(position);
            Logger.d(TAG, "New place " + holder.mItem.name);
            holder.mNameView.setText("* " + holder.mItem.name);
        } else {
            holder.mItem = mValues.get(position - mNewValues.size());
            holder.mNameView.setText(holder.mItem.name);
        }

        if (holder.mItem.primaryImageUrl != null) {
            Glide.with(mContext).load(holder.mItem.primaryImageUrl).into(holder.mThumbnail);
            Logger.d(TAG, "");
        } else if (holder.mItem.photoReferenceList != null && holder.mItem.photoReferenceList.size() > 0) {
            String url = "https://maps.googleapis.com/maps/api/place/photo?"
                    + "maxwidth=600"
                    + "&photoreference=" + holder.mItem.photoReferenceList.get(0)
                    + "&key=" + mContext.getResources().getString(R.string.google_api_key);
            Glide.with(mContext).load(url).into(holder.mThumbnail);
        }

        if(holder.mItem.postedById != null && holder.mItem.postedById.equals(myUserId)) {
            holder.mDelete.setVisibility(View.VISIBLE);
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HttpHandler httpHandler = new HttpHandler(mContext);
                    httpHandler.removeHangout(holder.mItem.id);
                }
            });
        } else {
            holder.mDelete.setVisibility(View.GONE);
        }
        holder.mDetails.setText(holder.mItem.details);
        holder.mTotalLikes.setText(holder.mItem.likes + " Likes | ");
        holder.mTotalComments.setText(holder.mItem.comments + " Comments");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPlaceClick(holder.mItem, AppConfigs.ACTION_VIEW);
                }
            }
        });
        holder.mDoCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onPlaceClick(holder.mItem, AppConfigs.ACTION_VIEW);
                }
            }
        });

        holder.mDoShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPlaceClick(holder.mItem, AppConfigs.ACTION_VIEW);
            }
        });

        holder.mDoLikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPlaceClick(holder.mItem, AppConfigs.ACTION_VIEW);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewValues.size() + mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //     public final TextView mIdView;
        public final TextView mNameView;
        public final TextView mTotalLikes;
        public final TextView mTotalComments;
        public final TextView mDetails;
        public final ImageView mThumbnail;

        public final View mDoCommentView;
        public final View mDoShareView;
        public final View mDoLikeView;
        public final View mDelete;

        public Place mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mNameView = (TextView) view.findViewById(R.id.name);
            mTotalComments = (TextView) view.findViewById(R.id.total_comments);
            mTotalLikes = (TextView) view.findViewById(R.id.total_likes);
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            mDetails = (TextView) view.findViewById(R.id.detail);

            mDoLikeView = (View) view.findViewById(R.id.do_like);
            mDoCommentView = (View) view.findViewById(R.id.do_comment);
            mDoShareView = (View) view.findViewById(R.id.do_share);
            mDelete = view.findViewById(R.id.delete);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
