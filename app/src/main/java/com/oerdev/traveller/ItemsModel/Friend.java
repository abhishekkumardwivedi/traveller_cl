package com.oerdev.traveller.ItemsModel;

import com.oerdev.traveller.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.util.HashMap;

/**
 * Created by abhishek on 9/9/16.
 */
public class Friend {

    private final String ID = "id";
    private final String NAME = "name";
    private final String IMAGE = "image";
    private final String FACEBOOK_ID = "facebookId";
    private final String CONTACT_NUMBER = "phone";
    private final String CONTACT_EMAIL = "email";

    public String id;
    public String facebookId;
    public String name;
    public String contactNumber;
    public String contactId;
    public Bitmap photo;

    public HashMap<String, String> contact;

    public Friend() {
    }

    public Friend(String id, String name, Bitmap photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public Friend(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(ID);
            name = jsonObject.getString(NAME);
            facebookId = jsonObject.getString(FACEBOOK_ID);
            photo = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected JSONObject getFriendJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ID, id);
            jsonObject.put(NAME, name);
            jsonObject.put(IMAGE, photo);
            jsonObject.put(FACEBOOK_ID, facebookId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getThumbnailImageUrl() {
        return "https://graph.facebook.com/" + facebookId + "/picture?width=50&height=50";
    }

//    public Bitmap getSmallProfilePic(Context context) {
//        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.agra);
//        return getCroppedBitmap(icon, 25);
//    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / factor),
                    (int) (bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final String color = "#BAB399";
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
                radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }
}
