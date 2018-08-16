package leotik.labs.gesturemessenger.Util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import leotik.labs.gesturemessenger.POJO.ChatPOJO;

public class ChatsDatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "chats_db";
    private static final String CHAT_TABLE = "chats";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SENDER = "phone";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_STATUS = "status";


    public ChatsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CHAT_TABLE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SENDER + " TEXT,"
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


    public long insertChat(String sender, String message, String time, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_TIME, time);
        long id = db.insert(CHAT_TABLE, null, values);
        db.close();
        return id;
    }

    public void updateStatus(String sender, String time, String status) {
        //todo
    }

    public List<String> getChatUsers() {
        List<String> ChatUsers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CHAT_TABLE,
                new String[]{COLUMN_SENDER},
                null,
                null, COLUMN_MESSAGE, null, COLUMN_TIME + " DESC ", null);

        if (cursor.moveToFirst()) {
            do {
                ChatUsers.add(cursor.getString(cursor.getColumnIndex(COLUMN_SENDER)));
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return ChatUsers;
    }

    public ArrayList<ChatPOJO> getChats() {
        ArrayList<ChatPOJO> chats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CHAT_TABLE,
                new String[]{COLUMN_SENDER, COLUMN_STATUS, COLUMN_TIME, COLUMN_MESSAGE},
                null,
                null, null, null, COLUMN_TIME + " DESC ", null);

        if (cursor.moveToFirst()) {
            do {
                ChatPOJO chat = new ChatPOJO();
                chat.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_SENDER)));
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


}


