package com.oerdev.traveller.ItemsModel;

import com.oerdev.traveller.app.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


/**
 * Created by abhishek on 9/9/16.
 */
public class Place implements Parcelable {
    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String LAT = "latitude";
    private static final String LNG = "longitude";
    private static final String PRIMARY_IMAGE = "primaryImage";
    private static final String LIKE_COUNT = "likeCount";
    private static final String COMMENT_COUNT = "commentCount";
    private static final String DETAILS = "details";
    private static final String SN_DOC_ID = "snDocId";
    private static final String GOOGLE_PLACE_ID = "googlePlaceId";
    private static final String GOOGLE_PHOTO_REF = "googleImageRef";
    private static final String POSTED_BY = "postedBy";
    private static final String TAG = Place.class.getSimpleName();
    public String id;
    public String postedById;
    public String googlePlaceId;
    public String name;
    public double latitude;
    public double longitude;
    public String details;
    public int likes;
    public int comments;
    public String primaryImageUrl;
    public ArrayList<String> photoReferenceList;
    public String snDocId;
    private JSONObject jobj = null;

    public Place(String id, String name, String details, int likes, int comments, String image,
                 double latitude, double longitude, String snDocId) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.likes = likes;
        this.comments = comments;
        this.primaryImageUrl = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.snDocId = snDocId;
    }

    public Place(JSONObject jobj) throws JSONException {
        Logger.d(TAG, "Place: " + jobj.toString());
        this.jobj = jobj;
        this.id = this.getString(ID);
        this.name = this.getString(NAME);
        this.latitude = Double.parseDouble(this.getString(LAT));
        this.longitude = Double.parseDouble(this.getString(LNG));
        this.primaryImageUrl = this.getString(PRIMARY_IMAGE);
        this.likes = this.getInt(LIKE_COUNT);
        this.comments = this.getInt(COMMENT_COUNT);
        this.details = this.getString(DETAILS);
        this.snDocId = this.getString(SN_DOC_ID);
        this.googlePlaceId = this.getString(GOOGLE_PLACE_ID);
        this.postedById = this.getString(POSTED_BY);

        if (jobj.isNull(GOOGLE_PHOTO_REF)) {
            this.photoReferenceList = null;
        } else {
            Logger.d(TAG, "photos: " + jobj.get(GOOGLE_PHOTO_REF));
            JSONObject photos = new JSONObject(jobj.getString(GOOGLE_PHOTO_REF));
            this.photoReferenceList = new ArrayList<String>();
            for (int i = 0; i < photos.length(); i++) {
                this.photoReferenceList.add(i, photos.getString(Integer.toString(i)));
            }
        }
    }

    public Place(String gplaceId, String name, double latitude, double longitude, ArrayList<String> photosList) {
        this.googlePlaceId = gplaceId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoReferenceList = photosList;
    }

    public Place(Parcel in) {
        id = in.readString();
        googlePlaceId = in.readString();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        details = in.readString();
        likes = in.readInt();
        comments = in.readInt();
        primaryImageUrl = in.readString();
        snDocId = in.readString();
        photoReferenceList = in.readArrayList(null);
        postedById = in.readString();
    }

    public JSONObject getPlaceJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ID, id);
            jsonObject.put(NAME, name);
            jsonObject.put(LAT, Double.toString(latitude));
            jsonObject.put(LNG, Double.toString(longitude));
            jsonObject.put(PRIMARY_IMAGE, primaryImageUrl);
            jsonObject.put(LIKE_COUNT, likes);
            jsonObject.put(COMMENT_COUNT, comments);
            jsonObject.put(DETAILS, details);
            jsonObject.put(SN_DOC_ID, snDocId);
            jsonObject.put(GOOGLE_PLACE_ID, googlePlaceId);
            jsonObject.put(POSTED_BY, postedById);
            jsonObject.put(GOOGLE_PHOTO_REF, getGooglePhotoRef());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getGooglePhotoRef() {
        if (this.photoReferenceList == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < photoReferenceList.size(); i++) {
                jsonObject.put(Integer.toString(i), photoReferenceList.get(i));
            }
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getString(String key) throws JSONException {
        if (jobj.isNull(key))
            return null;
        else
            return jobj.getString(key);
    }

    private int getInt(String key) throws JSONException {
        if (jobj.isNull(key))
            return 0;
        else
            return jobj.getInt(key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(googlePlaceId);
        parcel.writeString(name);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(details);
        parcel.writeInt(likes);
        parcel.writeInt(comments);
        parcel.writeString(primaryImageUrl);
        parcel.writeString(snDocId);
        parcel.writeList(photoReferenceList);
        parcel.writeString(postedById);
    }
}
