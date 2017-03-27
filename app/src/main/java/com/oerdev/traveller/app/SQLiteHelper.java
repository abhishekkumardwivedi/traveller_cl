package com.oerdev.traveller.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abhishek on 24/8/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DB_NAME = "TravellerDB";
    private static final int DB_VERSION = 1;

    // Contacts table name
    private static final String TABLE_PROFILE = "profile";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String IMAGE = "image";

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_PROFILE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"
                + EMAIL + " TEXT" + ")";
        db.execSQL(CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        // Create tables again
        onCreate(db);
    }
}
