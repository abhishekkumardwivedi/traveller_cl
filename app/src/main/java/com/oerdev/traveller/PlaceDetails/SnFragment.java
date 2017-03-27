package com.oerdev.traveller.PlaceDetails;

import com.oerdev.traveller.ItemsModel.Comment;
import com.oerdev.traveller.ItemsModel.Like;
import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.PlaceDetails.sn.CommentsRecyclerViewAdapter;
import com.oerdev.traveller.PlaceDetails.sn.LikesRecyclerViewAdapter;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.Logger;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abhishek on 17/9/16.
 */

public class SnFragment extends Fragment implements View.OnClickListener {

    public static final List<Like> LIKES_ITEMS = new ArrayList<Like>();
    public static final Map<String, Like> LIKES_ITEM_MAP = new HashMap<String, Like>();
    public static final List<Comment> COMMENTS_ITEMS = new ArrayList<Comment>();
    public static final Map<String, Comment> COMMENTS_ITEM_MAP = new HashMap<String, Comment>();
    private static final String TAG = SnFragment.class.getSimpleName();
    public static LikesRecyclerViewAdapter mLikeAdapter;
    public static CommentsRecyclerViewAdapter mCommentAdapter;
    public SnFIListener mSnFIListener;
    EditText commentEditText;
    Place mPlace;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPlace = ((DetailedActivity) getActivity()).getPlace();
        View rootView = inflater.inflate(R.layout.fragment_details_activity_sn_content, container, false);
        final int mColumnCountLike = 1;
        final int mColumnCountComment = 1;
        Logger.d(TAG, "onCreateView");

        View likeView = rootView.findViewById(R.id.likes_recycler_view);
        if (likeView instanceof RecyclerView) {
            Context context = likeView.getContext();
            RecyclerView recyclerView = (RecyclerView) likeView;

            if (mColumnCountLike <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCountLike));
            }

            mLikeAdapter = new LikesRecyclerViewAdapter(getContext(), LIKES_ITEMS);
            recyclerView.setAdapter(mLikeAdapter);
        }

        View commentView = rootView.findViewById(R.id.comments_recycler_view);
        if (commentView instanceof RecyclerView) {
            Context context = commentView.getContext();
            RecyclerView recyclerView = (RecyclerView) commentView;

            if (mColumnCountComment <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCountComment));
            }

            mCommentAdapter = new CommentsRecyclerViewAdapter(getContext(), COMMENTS_ITEMS);
            recyclerView.setAdapter(mCommentAdapter);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView likeImageView = (ImageView) view.findViewById(R.id.like_img);
        likeImageView.setOnClickListener(this);
        TextView commentButton = (TextView) view.findViewById(R.id.comment_btn);
        commentButton.setOnClickListener(this);
        commentEditText = (EditText) view.findViewById(R.id.comment_here);

        HttpHandler httpHandler = new HttpHandler(getContext());
        httpHandler.getSn(mPlace.id);
    }

    @Override
    public void onClick(View view) {
        HttpHandler httpHandler = new HttpHandler(getContext());
        switch (view.getId()) {
            case R.id.like_img:
                httpHandler.putLike(mPlace.id);
                break;
            case R.id.comment_btn:
                String message = String.valueOf(commentEditText.getText());
                httpHandler.putComment(mPlace.id, message);
                commentEditText.setText("");
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
                break;
            default:
                Logger.d(TAG, "click event not handled !!");
        }
    }

    /**
     * This could be later used to see user profile of selected user like/comment
     * from the recycler view.
     */
    interface SnFIListener {
        void onUserSelect(String userId);
    }
}
