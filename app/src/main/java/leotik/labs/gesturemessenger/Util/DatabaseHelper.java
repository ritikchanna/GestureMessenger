package leotik.labs.gesturemessenger.Util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import leotik.labs.gesturemessenger.POJO.UserPOJO;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "app_db";
    private static final String USER_INFO_TABLE = "users";
    private static final String USER_INFO_COLUMN_ID = "id";
    private static final String USER_INFO_COLUMN_EMAIL = "email";
    private static final String USER_INFO_COLUMN_NAME = "name";
    private static final String USER_INFO_COLUMN_PHOTO_URL = "photo";
    private static final String USER_INFO_COLUMN_PHONE = "phone";
    private static final String USER_INFO_COLUMN_STATUS = "status";
    private static final String USER_INFO_COLUMN_TIMESTAMP = "timestamp";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_INFO_TABLE + "("
                + USER_INFO_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_INFO_COLUMN_EMAIL + " TEXT,"
                + USER_INFO_COLUMN_NAME + " TEXT,"
                + USER_INFO_COLUMN_PHOTO_URL + " TEXT,"
                + USER_INFO_COLUMN_STATUS + " TEXT,"
                + USER_INFO_COLUMN_PHONE + " TEXT,"
                + USER_INFO_COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + USER_INFO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public long insertUser(String email, String name, String photoUrl, String phone, String status, @Nullable String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_INFO_COLUMN_EMAIL, email);
        values.put(USER_INFO_COLUMN_NAME, name);
        values.put(USER_INFO_COLUMN_PHOTO_URL, photoUrl);
        values.put(USER_INFO_COLUMN_PHONE, phone);
        values.put(USER_INFO_COLUMN_STATUS, status);
        if (timestamp != null)
            values.put(USER_INFO_COLUMN_TIMESTAMP, timestamp);
        long id = db.insert(USER_INFO_TABLE, null, values);
        db.close();
        return id;
    }

    public long insertUser(UserPOJO userPOJO, @Nullable String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_INFO_COLUMN_EMAIL, userPOJO.getE());
        values.put(USER_INFO_COLUMN_NAME, userPOJO.getN());
        values.put(USER_INFO_COLUMN_PHOTO_URL, userPOJO.getU());
        values.put(USER_INFO_COLUMN_PHONE, userPOJO.getP());
        values.put(USER_INFO_COLUMN_STATUS, userPOJO.getS());
        if (timestamp != null)
            values.put(USER_INFO_COLUMN_TIMESTAMP, timestamp);
        long id = db.insert(USER_INFO_TABLE, null, values);
        db.close();
        return id;
    }

    public UserPOJO getUser(String phone) {
        Log.d("Ritik", "getUser: " + phone);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_INFO_TABLE,
                new String[]{USER_INFO_COLUMN_EMAIL, USER_INFO_COLUMN_NAME, USER_INFO_COLUMN_PHOTO_URL, USER_INFO_COLUMN_PHONE, USER_INFO_COLUMN_TIMESTAMP, USER_INFO_COLUMN_STATUS},
                USER_INFO_COLUMN_PHONE + "=?",
                new String[]{phone}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();


        if (cursor.getCount() == 0)
            return null;

        return new UserPOJO(cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_PHOTO_URL)),
                cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_PHONE)),
                cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_STATUS)));
    }

    public List<UserPOJO> getAllUsers_old() {
        List<UserPOJO> users = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + USER_INFO_TABLE + " ORDER BY " +
                USER_INFO_COLUMN_NAME + " ASC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserPOJO user = new UserPOJO(cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_PHOTO_URL)),
                        cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_STATUS)));

                users.add(user);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return users;
    }

    public List<UserPOJO> getAllUsers(@Nullable String Status) {
        List<UserPOJO> users = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(USER_INFO_TABLE,
                new String[]{USER_INFO_COLUMN_EMAIL, USER_INFO_COLUMN_NAME, USER_INFO_COLUMN_PHOTO_URL, USER_INFO_COLUMN_PHONE, USER_INFO_COLUMN_STATUS},
                USER_INFO_COLUMN_STATUS + "=?",
                new String[]{Status}, null, null, USER_INFO_COLUMN_NAME + " ASC ", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserPOJO user = new UserPOJO(cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_PHOTO_URL)),
                        cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_STATUS)));

                users.add(user);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return users;
    }


    public List<String> getallPhone() {

        List<String> users = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  " + USER_INFO_COLUMN_PHONE + " FROM " + USER_INFO_TABLE + " ORDER BY " +
                USER_INFO_COLUMN_NAME + " ASC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {


                users.add(cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_PHONE)));
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return users;
    }


    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + USER_INFO_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(USER_INFO_TABLE, null, null);
    }

    public String getlastUserupdate() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_INFO_TABLE,
                new String[]{USER_INFO_COLUMN_TIMESTAMP},
                null, null, null, null, USER_INFO_COLUMN_TIMESTAMP + " DESC ", String.valueOf(1));

        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(USER_INFO_COLUMN_TIMESTAMP));
        } else return "0";

    }

    public void deleteUser(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        UserPOJO user = getUser(phone);
        cv.put(USER_INFO_COLUMN_NAME, user.getN());
        cv.put(USER_INFO_COLUMN_PHOTO_URL, user.getU());
        cv.put(USER_INFO_COLUMN_PHONE, user.getP());
        cv.put(USER_INFO_COLUMN_EMAIL, user.getE());
        cv.put(USER_INFO_COLUMN_STATUS, "x"); //These Fields should be your String values of actual column names
        db.update(USER_INFO_TABLE, cv, USER_INFO_COLUMN_PHONE + " =? ", new String[]{phone});
    }

    public void updateUser(UserPOJO user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_INFO_COLUMN_NAME, user.getN());
        cv.put(USER_INFO_COLUMN_PHOTO_URL, user.getU());
        cv.put(USER_INFO_COLUMN_PHONE, user.getP());
        cv.put(USER_INFO_COLUMN_EMAIL, user.getE());
        cv.put(USER_INFO_COLUMN_STATUS, user.getS()); //These Fields should be your String values of actual column names
        db.update(USER_INFO_TABLE, cv, USER_INFO_COLUMN_PHONE + " =? ", new String[]{user.getP()});
    }


}


