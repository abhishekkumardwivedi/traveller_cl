package com.oerdev.traveller.Network;

import com.oerdev.traveller.Hangout.HangoutFragment;
import com.oerdev.traveller.Home.HomeTabbedActivity;
import com.oerdev.traveller.ItemsModel.Comment;
import com.oerdev.traveller.ItemsModel.Like;
import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.ItemsModel.Plan;
import com.oerdev.traveller.PlaceDetails.SnFragment;
import com.oerdev.traveller.R;
import com.oerdev.traveller.Suggest.SuggestFragment;
import com.oerdev.traveller.app.Logger;
import com.oerdev.traveller.app.SessionManager;
import com.oerdev.traveller.myPlan.MyPlanFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by abhishek on 24/8/16.
 */
public class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();
    private static final String REGISTER_USER = "register";
    private static final String LOGIN_USER = "Login";
    private static final String GET_SUGGEST = "suggest";
    private static final String CREATE_PLACE = "create_place_hangout";
    private static final String GET_HANGOUT = "hangout";
    private static final String DELETE_HANGOUT = "delete_hangout";
    private static final String SEND_MYPLAN = "update_myplan";
    private static final String GET_MYPLAN = "get_myplan";
    private static final String SEND_COMMENT = "send_comment";
    private static final String SEND_LIKE = "send_like";
    private static final String ADD_MYPLACE = "add_myplace";
    private static final String GET_SN = "get_sn";
    private static final String ERROR = "error";
    private static final String IOERROR = "ioerror";

    private static final String GET_GOOGLE_PLACE_JSON = "google_place_json";
    private Context mContext;

    public HttpHandler() {
    }

    public HttpHandler(Context context) {
        mContext = context;
    }

    public void registerUser(String name, String email, String id) {
        String url = AppConfigs.URL_USER_REGISTER;
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter(AppConfigs.EXTRA_NAME, name)
                .appendQueryParameter(AppConfigs.EXTRA_EMAIL, email)
                .appendQueryParameter(AppConfigs.EXTRA_TEST_ID, id);

        String query = builder.build().getEncodedQuery();

        new HttpPostAsyncDownloader().execute(url, query, REGISTER_USER);
    }

    public void registerUser(String idType, String id, String email, String name, String gender) {
        String url = AppConfigs.URL_USER_REGISTER + idType + "/" + id;
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter(AppConfigs.EXTRA_NAME, name)
                .appendQueryParameter(AppConfigs.EXTRA_EMAIL, email)
                .appendQueryParameter(AppConfigs.EXTRA_GENDER, gender);

        String query = builder.build().getEncodedQuery();

        new HttpPostAsyncDownloader().execute(url, query, REGISTER_USER);
    }

    public void loginUser(String key) {
        String url = AppConfigs.URL_USER_LOGIN +
                SessionManager.getInstance(HomeTabbedActivity.sContext).getUserId();
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter(AppConfigs.EXTRA_USE, key);
        String query = builder.build().getEncodedQuery();

        new HttpPostAsyncDownloader().execute(url, query, LOGIN_USER);
    }

    public void getUserData() {
    }

    public void getSuggestContent() {
        String url = AppConfigs.URL_GET_SUGGEST +
                SessionManager.getInstance(HomeTabbedActivity.sContext).getUserId();
        new HttpPostAsyncDownloader().execute(url, null, GET_SUGGEST);
    }

    public void newHangout(Context context, Place place) {
        String url = AppConfigs.URL_PLACE_CREATE +
                SessionManager.getInstance(context).getUserId();
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter(AppConfigs.EXTRA_NAME, place.name)
                .appendQueryParameter(AppConfigs.EXTRA_LATITUDE, Double.toString(place.latitude))
                .appendQueryParameter(AppConfigs.EXTRA_LONGITUDE, Double.toString(place.longitude))
                .appendQueryParameter(AppConfigs.EXTRA_GOOGLE_PLACE_ID, place.googlePlaceId)
                .appendQueryParameter(AppConfigs.EXTRA_GOOGLE_IMAGE_REF, place.getGooglePhotoRef().toString());
        String query = builder.build().getEncodedQuery();
        new HttpPostAsyncDownloader().execute(url, query, CREATE_PLACE);
    }

    public void removeHangout(String placeId) {
        String url = AppConfigs.URL_HANGOUT_DELETE
                + SessionManager.getInstance(mContext).getUserId()
                + "/" + placeId;
        new HttpPostAsyncDownloader().execute(url, null, DELETE_HANGOUT);
    }

    public void getHangoutContent() {
        String url = AppConfigs.URL_GET_HANGOUT
                + SessionManager.getInstance(HomeTabbedActivity.sContext).getUserId();
        new HttpPostAsyncDownloader().execute(url, null, GET_HANGOUT);
    }

    public void getSn(String placeId) {

        String url = AppConfigs.URL_GET_SN_ALL + SessionManager.getInstance(mContext).getUserId()
                + "/" + placeId;
        new HttpPostAsyncDownloader().execute(url, null, GET_SN);
    }

    public void putComment(String placeId, String comment) {
        String url = AppConfigs.URL_COMMENT_PLACE + SessionManager.getInstance(mContext).getUserId()
                + "/" + placeId;

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter(AppConfigs.EXTRA_COMMENT, comment)
                .appendQueryParameter(AppConfigs.EXTRA_FB_ID, SessionManager.getInstance(mContext).getFacebookId());
        String query = builder.build().getEncodedQuery();
        new HttpPostAsyncDownloader().execute(url, query, SEND_COMMENT);
    }

    public void putLike(String placeId) {
        String url = AppConfigs.URL_LIKE_PLACE + SessionManager.getInstance(mContext).getUserId()
                + "/" + placeId;
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter(AppConfigs.EXTRA_FB_ID, SessionManager.getInstance(mContext).getFacebookId());
        String query = builder.build().getEncodedQuery();
        new HttpPostAsyncDownloader().execute(url, query, SEND_LIKE);
    }

    public void updateMyPlan() {
        String url = AppConfigs.URL_UPDATE_MYPLAN + SessionManager.getInstance(mContext).getUserId();
        String plan;
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < MyPlanFragment.PLANS_ITEMS.size(); i++) {
                jsonArray.put(i, MyPlanFragment.PLANS_ITEMS.get(i).getPlanJson());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("plan", jsonArray);
            plan = jsonObject.toString();
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter(AppConfigs.PLAN, plan);
            String query = builder.build().getEncodedQuery();
            new HttpPostAsyncDownloader().execute(url, query, SEND_MYPLAN);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    public void getMyPlan() {
        String url = AppConfigs.URL_GET_MYPLAN + SessionManager.getInstance(mContext).getUserId();
        new HttpPostAsyncDownloader().execute(url, null, GET_MYPLAN);
    }

    public void getGooglePlaceDetailsJson(String placeId) {
        final String URL_GOOGLE_PLACE_DETAILS_BASE = "https://maps.googleapis.com/maps/api/place/details/json?";
        String googlePlaceUrl = URL_GOOGLE_PLACE_DETAILS_BASE +
                "placeid=" + placeId +
                "&key=" + mContext.getResources().getString(R.string.google_api_key);

        new HttpPostAsyncDownloader().execute(googlePlaceUrl, null, GET_GOOGLE_PLACE_JSON);
    }

    private interface httpResponseCallback {
        String callback();
    }

    protected class AppConfigs {
        public static final String SERVER = "http://139.59.8.130:3000/api";

        public static final String URL_USER_REGISTER = SERVER + "/user/register/";
        public static final String URL_USER_LOGIN = SERVER + "/user/Login/";
        public static final String URL_GET_ALL_USERS = SERVER + "/user/get/all";
        public static final String URL_GET_SUGGEST = SERVER + "/place/suggest/get/";
        public static final String URL_GET_HANGOUT = SERVER + "/place/hangout/get/";
        public static final String URL_PLACE_CREATE = SERVER + "/place/create/";
        public static final String URL_HANGOUT_DELETE = SERVER + "/place/hangout/delete/";
        public static final String URL_GET_PLACE_BYID = SERVER + "/place/get/";

        public static final String URL_GET_SN_ALL = SERVER + "/sn/get/";
        public static final String URL_USER_ADD_FRIENDS = SERVER + "/user/friends/add/";
        public static final String URL_USER_ADD_MYPLAN = SERVER + "/user/myplan/add/";
        public static final String URL_USER_GET_MYPLAN = SERVER + "/user/myplan/get/";
        public static final String URL_USER_MYPLAN_DATE = SERVER + "/user/myplan/date/";
        public static final String URL_LIKE_PLACE = SERVER + "/sn/like/";
        public static final String URL_COMMENT_PLACE = SERVER + "/sn/comment/";
        public static final String URL_UPDATE_MYPLAN = SERVER + "/user/myplan/add/";
        public static final String URL_GET_MYPLAN = SERVER + "/user/myplan/get/";
        public static final String URL_ADD_NEW_PLACE = SERVER + "/place/create/";

        public static final String KEY_PLACE_ID = "place_id";
        public static final String EXTRA_GOOGLE_PLACE_ID = "google_place_id";
        public static final String EXTRA_GOOGLE_IMAGE_REF = "g_image_ref";
        public static final String EXTRA_LONGITUDE = "longitude";
        public static final String EXTRA_LATITUDE = "latitude";
        public static final String EXTRA_DETAILS = "details";
        public static final String EXTRA_imageURL = "imageUrl";
        public static final String EXTRA_NAME = "name";
        public static final String EXTRA_EMAIL = "email";
        public static final String EXTRA_GENDER = "gender";
        public static final String EXTRA_IMAGE = "profile_image";
        public static final String EXTRA_TEST_ID = "test_id";
        public static final String EXTRA_COMMENT = "comment";
        public static final String EXTRA_FB_ID = "fid";
        public static final String EXTRA_DO_ACTION = "update";
        public static final String ADD = "add";
        public static final String REMOVE = "remove";
        public static final String FRIENDS = "friends";
        public static final String DATE = "date";
        public static final String EXTRA_USE = "use";
        public static final String PLAN = "plan";


    }

    /**
     * String JSONArrayString = HttpAsyncDownloader().execute(String URL, String query);
     */
    class HttpPostAsyncDownloader extends AsyncTask<String, Void, String> {
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;
        private String request = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                Logger.d(TAG, ">>>> " + params[0] + "  [ " + params[1] + " ]");
                URL url = new URL(params[0]);
                request = params[2];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                if (params[1] != null) {
                    writer.write(params[1]);
                }
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                int response_code = conn.getResponseCode();
                Logger.d(TAG, "<<<< " + response_code);

                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Logger.d(TAG, "<<<< " + result.toString());
                    return result.toString();
                } else {
                    return Integer.toString(response_code);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return ERROR;
            } catch (IOException e) {
                e.printStackTrace();
                Logger.d(TAG, "Message: " + e.getMessage());
                Logger.d(TAG, "Localized: " + e.getLocalizedMessage());
                return IOERROR;
            }
        }

        @Override
        protected void onPostExecute(String jArrayString) {
            if (jArrayString.equals(ERROR)) return;
            if (jArrayString.equals(IOERROR)) {
                Toast.makeText(mContext,
                        "Connection failed !! \nCheck data connection and restart app", Toast.LENGTH_LONG).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 2000);
                return;
            }
            switch (request) {
                case REGISTER_USER:
                    parseUserRegisterJsonArray(jArrayString);
                    break;
                case LOGIN_USER:
                    parseUserLoginJsonArray(jArrayString);
                    break;
                case GET_SUGGEST:
                    parseSuggestJsonArray(jArrayString);
                    if (SuggestFragment.mAdapter != null) {
                        SuggestFragment.mAdapter.notifyDataSetChanged();
                    }
                    break;
                case CREATE_PLACE:
                    parseNewPlaceAddedJsonArray(jArrayString);
                    if (HangoutFragment.mAdapter != null) {
                        HangoutFragment.mAdapter.notifyDataSetChanged();
                    }
                    break;
                case GET_HANGOUT:
                case DELETE_HANGOUT:
                    parseHangoutJsonArray(jArrayString);
                    if (HangoutFragment.mAdapter != null) {
                        HangoutFragment.mAdapter.notifyDataSetChanged();
                    }
                    break;
                case SEND_MYPLAN:
                case GET_MYPLAN:
                    parseMyPlanJsonArray(jArrayString);
                    if (MyPlanFragment.mAdapter != null) {
                        MyPlanFragment.mAdapter.notifyDataSetChanged();
                    }
                    break;
                case GET_SN:
                    parseSNJsonArray(jArrayString);
                    SnFragment.mLikeAdapter.notifyDataSetChanged();
                    SnFragment.mCommentAdapter.notifyDataSetChanged();
                    break;
                case SEND_COMMENT:
                    parseCommentJsonArray(jArrayString);
                    SnFragment.mCommentAdapter.notifyDataSetChanged();
                    break;
                case SEND_LIKE:
                    parseLikeJsonArray(jArrayString);
                    SnFragment.mLikeAdapter.notifyDataSetChanged();
                    break;
                case GET_GOOGLE_PLACE_JSON:
                    parseGooglePlaceJson(jArrayString);
                    if (HangoutFragment.mAdapter != null) {
                        HangoutFragment.mAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    orphenRequest();
            }
        }

        private void parseUserRegisterJsonArray(String jArrayString) {
            try {
                if (jArrayString.equals("error")) {
                    Logger.d(TAG, "Register failed!!");
                    return;
                }
                JSONArray jsonArray = new JSONArray(jArrayString);
                JSONObject jobj = jsonArray.getJSONObject(0);

                if (jobj != null) {
                    SessionManager session = SessionManager.getInstance(mContext);
                    String id = jobj.getString("_id");
                    String facebookd = jobj.getString("facebookId");
                    String name = jobj.getString("name");
                    String email = jobj.getString("email");
                    session.saveLoginSession(id, facebookd, name, email);
                    Logger.d(TAG, "user id saved to session: " + session.getUserId());
                    Intent intent = new Intent(mContext, HomeTabbedActivity.class);
                    mContext.startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseUserLoginJsonArray(String jArrayString) {
            if (jArrayString == null) {
                SessionManager.getInstance(mContext).logoutUser();
                return;
            }
            Intent intent = new Intent(mContext, HomeTabbedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }

        private void parseSuggestJsonArray(String jArrayString) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(jArrayString);
                JSONObject jobj;
                Place suggestItem;
                if (jsonArray != null) {
                    SuggestFragment.SUGGEST_ITEMS.clear();
                    SuggestFragment.SUGGEST_ITEM_MAP.clear();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    jobj = (JSONObject) jsonArray.get(i);
                    suggestItem = new Place(jobj);
                    SuggestFragment.SUGGEST_ITEMS.add(suggestItem);
                    SuggestFragment.SUGGEST_ITEM_MAP.put(suggestItem.id, suggestItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseNewPlaceAddedJsonArray(String jobjectString) {
            try {
                JSONObject jobj = new JSONObject(jobjectString);
                Place place = new Place(jobj);
                HangoutFragment.HANGOUT_ITEMS.add(place);
                HangoutFragment.HANGOUT_ITEM_MAP.put(place.id, place);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseHangoutJsonArray(String jArrayString) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(jArrayString);
                JSONObject jobj;
                Place suggestItem;
                if (jsonArray != null) {
                    HangoutFragment.HANGOUT_ITEMS.clear();
                    HangoutFragment.HANGOUT_ITEM_MAP.clear();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    jobj = (JSONObject) jsonArray.get(i);
                    suggestItem = new Place(jobj);
                    HangoutFragment.HANGOUT_ITEMS.add(suggestItem);
                    HangoutFragment.HANGOUT_ITEM_MAP.put(suggestItem.id, suggestItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseMyPlanJsonArray(String jArrayString) {

            if (jArrayString.equals(null)) {
                MyPlanFragment.PLANS_ITEMS.clear();
                MyPlanFragment.PLACE_PLAN_MAP.clear();
                return;
            }
            try {

                JSONObject j = new JSONObject(jArrayString);
                JSONArray planArray = j.getJSONArray("plan");
                JSONObject jobj;
                Plan planItem;
                if (planArray != null) {
                    MyPlanFragment.PLANS_ITEMS.clear();
                    MyPlanFragment.PLACE_PLAN_MAP.clear();
                }
                for (int i = 0; i < planArray.length(); i++) {
                    jobj = (JSONObject) planArray.get(i);
                    planItem = new Plan(jobj);
                    MyPlanFragment.PLANS_ITEMS.add(planItem);
                    MyPlanFragment.PLACE_PLAN_MAP.put(planItem.mPlace.id, planItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseSNJsonArray(String j) {
            try {
                JSONObject jobj = new JSONObject(j);
                parseCommentJsonArray(jobj.toString());
                parseLikeJsonArray(jobj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseLikeJsonArray(String j) {
            try {
                JSONObject jobj = new JSONObject(j);
                JSONArray jsonLikes = jobj.getJSONArray("likes");
                SnFragment.LIKES_ITEMS.clear();
                SnFragment.LIKES_ITEM_MAP.clear();
                JSONObject jobjLike;
                for (int i = 0; i < jsonLikes.length(); i++) {
                    jobjLike = jsonLikes.getJSONObject(i);
                    Like like = new Like(jobjLike);
                    SnFragment.LIKES_ITEMS.add(like);
                    SnFragment.LIKES_ITEM_MAP.put(like.id, like);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseCommentJsonArray(String j) {
            try {
                JSONObject jobj = new JSONObject(j);
                JSONArray jsonComment = jobj.getJSONArray("comments");
                SnFragment.COMMENTS_ITEMS.clear();
                SnFragment.COMMENTS_ITEM_MAP.clear();
                JSONObject jobjComment;
                for (int i = 0; i < jsonComment.length(); i++) {
                    jobjComment = jsonComment.getJSONObject(i);
                    Comment comment = new Comment(jobjComment);
                    SnFragment.COMMENTS_ITEMS.add(comment);
                    SnFragment.COMMENTS_ITEM_MAP.put(comment.id, comment);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseGooglePlaceJson(String j) {
            try {
                JSONObject jobj = new JSONObject(j);
                JSONObject result = jobj.getJSONObject("result");
                JSONArray photosJarry = result.getJSONArray("photos");
                String placeId = result.getString("place_id");
                String placeName = result.getString("name");
                String lat = result.getJSONObject("geometry").getJSONObject("location").getString("lat");
                String lng = result.getJSONObject("geometry").getJSONObject("location").getString("lng");
                if (HangoutFragment.HANGOUT_ITEM_MAP.get(placeId) != null) {
                    Toast.makeText(mContext, "Place " + placeName + " already in conversation!!", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<String> photosList = new ArrayList<>();
                    for (int i = 0; i < photosJarry.length(); i++) {
                        photosList.add(photosJarry.getJSONObject(i).getString("photo_reference"));
                    }
                    Place place = new Place(placeId, placeName,
                            Double.parseDouble(lat), Double.parseDouble(lng), photosList);
//                    HangoutFragment.HANGOUT_NEW_ITEMS.add(place);
//                    HangoutFragment.HANGOUT_NEW_ITEM_MAP.put(placeId, place);
                    newHangout(mContext, place);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * public Sn(JSONObject sn) throws JSONException {
         * mLikes = sn.getJSONArray("likes");
         * mComments = sn.getJSONArray("comments");
         * }
         *
         * public Like getLike(int index) throws JSONException {
         * JSONObject jobj = mLikes.getJSONObject(index);
         * Like like = new Like(
         * jobj.getString("_id"),
         * jobj.getString("name"),
         * jobj.getLong("date"));
         * return like;
         * }
         *
         * public Comment getComment(int index) throws JSONException {
         * JSONObject jobj = mComments.getJSONObject(index);
         * Comment comment = new Comment(
         * jobj.getString("_id"),
         * jobj.getString("name"),
         * jobj.getLong("date"),
         * jobj.getString("comment"));
         *
         * return comment;
         * }
         */

        private void orphenRequest() {
            Logger.d(TAG, "UNHANDLED ORPHEN REQUEST !!!!!!!");
        }
    }
}
