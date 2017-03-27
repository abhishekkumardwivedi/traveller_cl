package com.oerdev.traveller.ItemsModel;

import com.oerdev.traveller.app.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by abhishek on 9/9/16.
 */
public class Plan {

    private static final String TAG = Plan.class.getSimpleName();

    private static final String PLACE = "place";
    private static final String DATE = "planDate";
    private static final String FRIENDS = "friends";
    private static final String ROUTE = "route";

    public Place mPlace;
    public String mDate;
    public ArrayList<Friend> mFriendsList;
    public ArrayList<Route> mRoutesList;


    public Plan(Place place) {
        mPlace = place;
        mDate = null;
        mFriendsList = new ArrayList<>();
        mRoutesList = new ArrayList<>();
    }

    public Plan(JSONObject jobjPlan) {
        Logger.d(TAG, jobjPlan.toString());
        try {
            mPlace = new Place(jobjPlan.getJSONObject(PLACE));
            mDate = jobjPlan.has(DATE) ? jobjPlan.getString(DATE) : null;
            mFriendsList = new ArrayList<>();
            mRoutesList = new ArrayList<>();

            JSONObject friendsJsonObject = jobjPlan.has(FRIENDS) ? jobjPlan.getJSONObject(FRIENDS) : null;
            JSONObject routeJsonObject = jobjPlan.has(ROUTE) ? jobjPlan.getJSONObject(ROUTE) : null;

            for (int i = 0; friendsJsonObject != null && i < friendsJsonObject.length(); i++) {
                mFriendsList.add(i, new Friend(friendsJsonObject.getJSONObject(Integer.toString(i))));
            }

            for (int i = 0; routeJsonObject != null && i < routeJsonObject.length(); i++) {
                mRoutesList.add(i, new Route(routeJsonObject.getJSONObject(Integer.toString(i))));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getPlanJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(PLACE, mPlace.getPlaceJson());
            jsonObject.put(DATE, mDate);

            JSONObject routeJsonObject = new JSONObject();
            for (int i = 0; i < mRoutesList.size(); i++) {
                routeJsonObject.put(Integer.toString(i), mRoutesList.get(i).getRouteJson());
            }
            jsonObject.put(ROUTE, routeJsonObject);

            JSONObject friendsJsonObject = new JSONObject();
            for (int i = 0; i < mFriendsList.size(); i++) {
                friendsJsonObject.put(Integer.toString(i), mFriendsList.get(i).getFriendJson());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getDateDdmmyyyy() {
        if (mDate == null) return null;
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(mDate));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(c.getTime());
    }

    public String getDateMs() {
        return mDate;
    }

    public ArrayList<Route> getRoutes() {
        if (!mRoutesList.isEmpty()) {
            return mRoutesList;
        } else {
            return null;
        }
    }

    public void removeDate() {
        mDate = null;
    }

    public void addFriend(String id, String name, Bitmap image) {
        Friend friend = new Friend(id, name, image);
        mFriendsList.add(mFriendsList.size(), friend);
    }

    public void removeFriend(String id) {
        for (int i = 0; i < mFriendsList.size(); i++) {
            if (mFriendsList.get(i).id.equals(id)) {
                mFriendsList.remove(i);
            }
        }
    }

    public void addRoute(String lat, String lng, String address, String gid) {
        Route route = new Route();
        route.lat = lat;
        route.lng = lng;
        route.address = address;
        route.googleId = gid;
        mRoutesList.add(mRoutesList == null ? 0 : mRoutesList.size(), route);
    }

    public void removeRoute(String gid) {
        for (int i = 0; i < mRoutesList.size(); i++) {
            if (mRoutesList.get(i).googleId.equals(gid)) {
                mRoutesList.remove(i);
            }
        }
    }

    public class Route {
        private final String LAT = "lat";
        private final String LNG = "lng";
        private final String ADDRESS = "address";
        private final String GOOGLE_PLACE_ID = "google_id";

        public String lat;
        public String lng;
        public String address;
        public String googleId;

        public Route() {
        }

        public Route(String la, String ln, String addr, String gid) {
            lat = la;
            lng = ln;
            address = addr;
            googleId = gid;
        }

        public Route(JSONObject jsonObject) {
            try {
                lat = jsonObject.getString(LAT);
                lng = jsonObject.getString(LNG);
                address = jsonObject.getString(ADDRESS);
                googleId = jsonObject.getString(GOOGLE_PLACE_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected JSONObject getRouteJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(LAT, lat);
                jsonObject.put(LNG, lng);
                jsonObject.put(ADDRESS, address);
                jsonObject.put(GOOGLE_PLACE_ID, googleId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }
}

