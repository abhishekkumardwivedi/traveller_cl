package com.oerdev.traveller.Suggest;

import com.bumptech.glide.Glide;
import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.AppConfigs;
import com.oerdev.traveller.app.Logger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abhishek on 25/8/16.
 */
public class SuggestRecyclerViewAdapter extends RecyclerView.Adapter<SuggestRecyclerViewAdapter.ViewHolder> {

    private final List<Place> mValues;
    private final SuggestFragment.SuggestFIListener mListener;
    private final Context mContext;

    public SuggestRecyclerViewAdapter(Context context, List<Place> items, SuggestFragment.SuggestFIListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggest_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        if (holder.mItem.primaryImageUrl != null) {
            Glide.with(mContext).load(holder.mItem.primaryImageUrl).into(holder.mThumbnail);
        } else if (holder.mItem.photoReferenceList != null && holder.mItem.photoReferenceList.size() > 0) {
            String url = "https://maps.googleapis.com/maps/api/place/photo?"
                    + "maxwidth=300"
                    + "&photoreference=" + holder.mItem.photoReferenceList.get(0)
                    + "&key=" + mContext.getResources().getString(R.string.google_api_key);
            Glide.with(mContext).load(url).into(holder.mThumbnail);
        }
        holder.mLikeView.setText("" + mValues.get(position).likes);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onPlaceClick(mValues.get(position), AppConfigs.ACTION_VIEW);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mLikeView;
        public final ImageView mThumbnail;

        public Place mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mLikeView = (TextView) view.findViewById(R.id.recommended);
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
