package com.oerdev.traveller.PlaceDetails.sn;

import com.alexzh.circleimageview.CircleImageView;
import com.alexzh.circleimageview.ItemSelectedListener;
import com.bumptech.glide.Glide;
import com.oerdev.traveller.ItemsModel.Comment;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.Logger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abhishek on 7/9/16.
 */
public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = CommentsRecyclerViewAdapter.class.getSimpleName();
    private final List<Comment> mValues;
    private Context mContext;

    public CommentsRecyclerViewAdapter(Context context, List<Comment> items) {
        mContext = context;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mItem = mValues.get(position);
        String comment = holder.mItem.comment;
        String name = holder.mItem.name;
        String time = holder.mItem.date;
        String dispTime = getTentativeDateDifference(System.currentTimeMillis() - Long.parseLong(time));

//            Logger.d(TAG, "comment: " + comment);
//            Logger.d(TAG, "name: " + name);
//            Logger.d(TAG, "time: "+ time);

        holder.mNameView.setText(name);
        holder.mCommentView.setText(comment);
        holder.mTime.setText(dispTime);
        if (holder.mItem.fid != null) {
            Glide.with(mContext).load(holder.mItem.getProfileImageUrlThumbnail()).into(holder.mPersonImage);

        } else {
            Glide.with(mContext).load(R.drawable.person_default).into(holder.mPersonImage);
        }
    }

    public String getTentativeDateDifference(long difference){

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        if(elapsedDays > 0) return elapsedDays + " days ago";
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;
        if(elapsedHours > 0) return elapsedHours + " hours ago";
        difference = difference % hoursInMilli;

        long elapsedMinutes = difference / minutesInMilli;
        if(elapsedMinutes > 0) return elapsedMinutes + " minutes ago";
        difference = difference % minutesInMilli;

        long elapsedSeconds = difference / secondsInMilli;

        return "just now";
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mCommentView;
        public final TextView mTime;
        public final CircleImageView mPersonImage;

        public Comment mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name_commented_by);
            mCommentView = (TextView) view.findViewById(R.id.comment_text);
            mTime = (TextView) view.findViewById(R.id.time);
            mPersonImage = (CircleImageView) view.findViewById(R.id.person_img_commented);

            mPersonImage.setOnItemSelectedClickListener(new ItemSelectedListener() {
                @Override
                public void onSelected(View view) {

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
