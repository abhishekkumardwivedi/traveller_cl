package com.oerdev.traveller.ItemsModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhishek on 17/9/16.
 */
public class Comment {
    public String id;
    public String fid;
    public String name;
    public String date;
    public String comment;

    public Comment(String id, String fid, String name, String date, String comment) {
        this.id = id;
        this.fid = fid;
        this.name = name;
        this.date = date;
        this.comment = comment;
    }

    public Comment(JSONObject jsonObject) {
        try {
            id = jsonObject.getString("userId");
            fid = jsonObject.isNull("f_id") ? null : jsonObject.getString("f_id");
            name = jsonObject.getString("userName");
            comment = jsonObject.getString("comment");
            date = jsonObject.getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getProfileImageUrlThumbnail() {
        return fid != null ? "https://graph.facebook.com/" + fid + "/picture?width=50&height=50" : null;
    }
}