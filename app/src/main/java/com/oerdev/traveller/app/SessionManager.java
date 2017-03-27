package com.oerdev.traveller.app;

import com.oerdev.traveller.Hangout.HangoutFragment;
import com.oerdev.traveller.Login.LoginActivity;
import com.oerdev.traveller.Suggest.SuggestFragment;
import com.oerdev.traveller.myPlan.MyPlanFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by abhishek on 17/8/16.
 */
public class SessionManager {

    public static final String SESSION_ID = "session_id";
    public static final String PROFILE_NAME = "phone";
    public static final String PROFILE_EMAIL = "pass";
    private static final String SHAREDPREF_NAME = "sharedpref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String PAST_LOCATION = "place";
    private static final String FACEBOOK_ID = "facebook_id";

    private static SessionManager instance = null;
    SharedPreferences pref;
    SharedPreferences.Editor mEditor;
    Context mContext;
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(SHAREDPREF_NAME, PRIVATE_MODE);
        mEditor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) instance = new SessionManager(context);
        return instance;
    }

    public void saveLoginSession(String id, String facebookId, String name, String email) {
        mEditor.putString(AppConfigs.KEY_USER_ID, id);
        mEditor.putString(PROFILE_NAME, name);
        mEditor.putString(PROFILE_EMAIL, email);
        mEditor.putString(FACEBOOK_ID, facebookId);
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.commit();
    }

    public String getFacebookId() {
        return pref.getString(FACEBOOK_ID, null);
    }

    public void savePastLocation(String place) {
        mEditor.putString(PAST_LOCATION, place);
        mEditor.commit();
    }

    public String getPastLocation() {
        return pref.getString(PAST_LOCATION, null);
    }

    public String getProfileName() {
        return pref.getString(PROFILE_NAME, null);
    }

    public String getProfileEmail() {
        return pref.getString(PROFILE_EMAIL, null);
    }

    public String getProfileImageUrl() {
        String id = pref.getString(FACEBOOK_ID, null);
        return "https://graph.facebook.com/" + id + "/picture?width=200&height=200";
    }

    public String getProfileImageUrlThumbnail() {
        String id = pref.getString(FACEBOOK_ID, null);
        return "https://graph.facebook.com/" + id + "/picture?width=50&height=50";
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // wipe shared preference
        mEditor.clear();
        mEditor.commit();
        HangoutFragment.logout();
        SuggestFragment.logout();
        MyPlanFragment.logout();
//        LoginManager.getInstance().logOut();

        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void saveImage(Context context, Bitmap b, String name, String extension) {
        name = name + "." + extension;
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImageBitmap(Context context, String name, String extension) {

        name = name + "." + extension;
        try {
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        } catch (Exception e) {
        }
        return null;
    }

    public void saveUserId(String id) {
        mEditor.putString(AppConfigs.KEY_USER_ID, id);
        mEditor.commit();
    }

    public String getUserId() {
        return pref.getString(AppConfigs.KEY_USER_ID, null);
    }
}
