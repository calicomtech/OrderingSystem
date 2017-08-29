package com.nutstechnologies.orderingsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by JAYBOB on 8/23/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "dbConString";

    // Table Names
    private static final String TABLE_CONSTRING = "tbl_connection";
    private static final String KEY_ID = "con_id";
    private static final String KEY_SERVER_NAME = "server_name";
    private static final String KEY_DATABASE_NAME = "db_name";
    private static final String KEY_USERNAME = "user_name";
    private static final String KEY_PASSWORD = "p_word";

    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_CONSTRING = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONSTRING + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_SERVER_NAME
            + " TEXT, " + KEY_DATABASE_NAME + " TEXT, " + KEY_USERNAME + " TEXT, " + KEY_PASSWORD + " TEXT)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_CONSTRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSTRING);
        // create new tables
        onCreate(db);
    }


    public void insertData(String servername, String dbname, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SERVER_NAME, servername);
        values.put(KEY_DATABASE_NAME, dbname);
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        db.insert(TABLE_CONSTRING, null, values);
        if (db != null && db.isOpen())
            db.close();
    }

    public void updateData(String servername, String dbname, String username, String password)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SERVER_NAME, servername);
        values.put(KEY_DATABASE_NAME, dbname);
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        // updating row
        db.update(TABLE_CONSTRING, values, KEY_ID + " = ?",
                new String[] { String.valueOf(1) });
        if (db != null && db.isOpen())
            db.close();
    }

    public ContentValues getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CONSTRING;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        ContentValues value = new ContentValues();
        if (c != null) {
            try {
                c.moveToFirst();
                value.put(KEY_SERVER_NAME, c.getString(c.getColumnIndex(KEY_SERVER_NAME)));
                value.put(KEY_DATABASE_NAME, c.getString(c.getColumnIndex(KEY_DATABASE_NAME)));
                value.put(KEY_USERNAME, c.getString(c.getColumnIndex(KEY_USERNAME)));
                value.put(KEY_PASSWORD, c.getString(c.getColumnIndex(KEY_PASSWORD)));
            } catch (Exception ex) {
                Log.v("Error on connection", ex.getMessage());
            }
        }
        if (db != null && db.isOpen())
            db.close();
        return value;
    }
}
