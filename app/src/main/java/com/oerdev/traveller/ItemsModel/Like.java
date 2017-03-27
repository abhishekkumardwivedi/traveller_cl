package com.oerdev.traveller.ItemsModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhishek on 17/9/16.
 */

public class Like {
    public String id;
    public String fid;
    public String name;
    public String date;

    public Like(String id, String fid, String name, String date) {
        this.id = id;
        this.fid = fid;
        this.name = name;
        this.date = date;
    }

    public Like(JSONObject jsonObject) {
        try {
            id = jsonObject.getString("userId");
            fid = jsonObject.isNull("f_id") ? null : jsonObject.getString("f_id");
            name = jsonObject.getString("userName");
            date = jsonObject.getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getProfileImageUrlThumbnail() {
        return fid != null ? "https://graph.facebook.com/" + fid + "/picture?width=50&height=50" : null;
    }
}