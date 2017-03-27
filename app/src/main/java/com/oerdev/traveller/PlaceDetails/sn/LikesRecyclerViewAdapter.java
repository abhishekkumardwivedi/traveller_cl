package com.oerdev.traveller.PlaceDetails.sn;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;
import com.bumptech.glide.Glide;
import com.oerdev.traveller.ItemsModel.Like;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.Logger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by abhishek on 7/9/16.
 */
public class LikesRecyclerViewAdapter extends RecyclerView.Adapter<LikesRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = LikesRecyclerViewAdapter.class.getSimpleName();

    private final List<Like> mValues;
    protected Context mContext;

    public LikesRecyclerViewAdapter(Context context, List items) {
        mContext = context;
        mValues = items;
        Logger.d(TAG, "LikesRecyclerViewAdapter: " + items.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.like_items, parent, false);
        Logger.d(TAG, "onCreateViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(holder.mItem.name);
        if(holder.mItem.fid != null) {
            Logger.d(TAG, "Image:" + holder.mItem.getProfileImageUrlThumbnail());
            Glide.with(mContext).load(holder.mItem.getProfileImageUrlThumbnail()).into(holder.mCircleImageView);
        } else {
            Glide.with(mContext).load(R.drawable.person_default).into(holder.mCircleImageView);
        }

        Logger.d(TAG, "onBindViewHolder: position = " + position);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final CircleImageView mCircleImageView;

        public Like mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name_liked_by);
            mCircleImageView = (CircleImageView) view.findViewById(R.id.person_img_liked);
            mCircleImageView.setOnItemSelectedClickListener(new ItemSelectedListener() {
                @Override
                public void onSelected(View view) {
                    Toast.makeText(mContext, mItem.name, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUnselected(View view) {

                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}

