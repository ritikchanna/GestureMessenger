package leotik.labs.gesturemessenger.Util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import leotik.labs.gesturemessenger.POJO.ChatPOJO;
import leotik.labs.gesturemessenger.POJO.UserPOJO;

public class ChatsDatabaseHelper extends SQLiteOpenHelper {

    public static final String STATUS_SENT = "s";
    public static final String STATUS_DELIVERED = "d";
    public static final String STATUS_READ = "r";
    public static final String STATUS_RECIEVED = "rr";
    public static final String SIDE_DOWN = "down";
    public static final String SIDE_UP = "up";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "chats_db";
    private static final String CHAT_TABLE = "chats";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_USER = "user";
    private static final String COLUMN_SIDE = "side";
    private DatabaseHelper databaseHelper;


    public ChatsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CHAT_TABLE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER + " TEXT,"
                + COLUMN_SIDE + " TEXT,"
                + COLUMN_MESSAGE + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_STATUS + " TEXT"
                + ")");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CHAT_TABLE);

        // Create tables again
        onCreate(db);
    }


    public long insertChat(String user, String side, String message, String time, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_USER, user);
        values.put(COLUMN_SIDE, side);
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_TIME, time);
        long id = db.insert(CHAT_TABLE, null, values);
        db.close();
        return id;
    }

    public void updateStatus(String sender, String time, String status) {
        //todo
    }

    public ArrayList<UserPOJO> getChatUsers() {
        ArrayList<UserPOJO> ChatUsers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CHAT_TABLE,
                new String[]{COLUMN_USER},
                null,
                null, COLUMN_USER, null, COLUMN_TIME + " DESC ", null);

        if (cursor.moveToFirst()) {
            do {
                UserPOJO user = databaseHelper.getUser(cursor.getString(cursor.getColumnIndex(COLUMN_USER)));
                ChatUsers.add(user);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return ChatUsers;
    }

    public String getLastMsgTime(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CHAT_TABLE,
                new String[]{COLUMN_TIME},
                COLUMN_USER + "=?",
                new String[]{user}, null, null, COLUMN_TIME + " DESC ", "1");

        if (cursor.moveToFirst()) {
            db.close();
            return cursor.getString(cursor.getColumnIndex(COLUMN_TIME));


        }
        db.close();
        return null;
    }

    public ArrayList<ChatPOJO> getChats() {
        ArrayList<ChatPOJO> chats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CHAT_TABLE,
                new String[]{COLUMN_USER, COLUMN_STATUS, COLUMN_TIME, COLUMN_MESSAGE},
                null,
                null, null, null, COLUMN_TIME + " DESC ", null);

        if (cursor.moveToFirst()) {
            do {
                ChatPOJO chat = new ChatPOJO();
                chat.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_USER)));
                chat.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)));
                chat.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                chat.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                chats.add(chat);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return chats;
    }


    public List<ChatPOJO> getChat(String phone) {
        List<ChatPOJO> chats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CHAT_TABLE,
                new String[]{COLUMN_USER, COLUMN_STATUS, COLUMN_TIME, COLUMN_MESSAGE, COLUMN_SIDE},
                COLUMN_USER + "=?",
                new String[]{phone}, null, null, COLUMN_TIME + " ASC ", null);

        if (cursor.moveToFirst()) {
            do {
                ChatPOJO chat = new ChatPOJO();
                chat.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_USER)));
                chat.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)));
                chat.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                chat.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                chat.setSide(cursor.getString(cursor.getColumnIndex(COLUMN_SIDE)));
                chats.add(chat);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return chats;
    }


}


