package com.example.afc.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.afc.chat.Message;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.afc.app.Config.DB_CHAT_TABLE;
import static com.example.afc.app.Config.DB_NAME;
import static com.example.afc.app.Config.DB_SEARCH_HISTORY_TABLE;

public class DBManagement extends SQLiteOpenHelper {
    private HashMap hp;
    SQLiteDatabase db;

    public DBManagement(Context context) {
        super(context, DB_NAME , null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_CHAT_TABLE + " (mess_id	INTEGER PRIMARY KEY AUTOINCREMENT, mess_to_id INTEGER, mess_from_id INTEGER, mess_text TEXT, mess_date DATE);");
        db.execSQL("CREATE TABLE " + DB_SEARCH_HISTORY_TABLE + " (search_id INTEGER PRIMARY KEY AUTOINCREMENT, search_text TEXT UNIQUE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean insertMessage (int from, int to, String text, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("mess_from_id", from);
        contentValues.put("mess_to_id", to);
        contentValues.put("mess_text", text);
        contentValues.put("mess_date", date);
        db.insert(DB_CHAT_TABLE, null, contentValues);
        return true;
    }

    public boolean insertSearch(String text){
        text = text.trim();
        if(text.length()==0) return true;

        ContentValues contentValues = new ContentValues();
        contentValues.put("search_text", text);
        db.replace(DB_SEARCH_HISTORY_TABLE, null, contentValues);
        return true;
    }

    public ArrayList<Message> getSavedMessages(int first_user_id, int second_user_id) {
        ArrayList<Message> mList = new ArrayList<Message>();
        Cursor res =  db.rawQuery( "select * from "+DB_CHAT_TABLE+" where mess_to_id="+ first_user_id +
                                        " and mess_from_id="+second_user_id+" or mess_to_id="+ second_user_id +
                                        " and mess_from_id="+first_user_id+
                                        " order by mess_id asc",
                            null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mList.add(new Message(
                    res.getInt(res.getColumnIndex("mess_id")),
                    res.getInt(res.getColumnIndex("mess_from_id")),
                    res.getInt(res.getColumnIndex("mess_to_id")),
                    res.getString(res.getColumnIndex("mess_text")),
                    res.getString(res.getColumnIndex("mess_date"))
            ));
            res.moveToNext();
        }
        return mList;
    }

    public ArrayList<String> getSearchHistory(String text) {
        ArrayList<String> mList = new ArrayList<String>();
        Cursor res =  db.rawQuery( "select * from "+DB_SEARCH_HISTORY_TABLE+" where search_text LIKE '%"+ text +
                        "%' order by search_id desc limit 4",
                null );
        if(res.getCount() > 0){
            res.moveToFirst();
            while(res.isAfterLast() == false){
                mList.add(res.getString(res.getColumnIndex("search_text")));
                res.moveToNext();
            }
        }
        return mList;
    }

    public Message getLastMessage(int from, int to){
        Message lastMessage;
        Cursor res =  db.rawQuery( "select * from "+DB_CHAT_TABLE+" where mess_to_id="+ to +
                        " and mess_from_id="+ from +" or mess_to_id="+ from +
                        " and mess_from_id="+ to +
                        " order by mess_id desc limit 1",
                null );
        if(res.getCount() == 0){
            return new Message(
                    0,
                    from,
                    to,
                    "",
                    ""
            );
        }
        res.moveToFirst();

        lastMessage = new Message(
                res.getInt(res.getColumnIndex("mess_id")),
                res.getInt(res.getColumnIndex("mess_from_id")),
                res.getInt(res.getColumnIndex("mess_to_id")),
                res.getString(res.getColumnIndex("mess_text")),
                res.getString(res.getColumnIndex("mess_date"))
        );
        return lastMessage;
    }

    public boolean clearTable(String table){
        db.execSQL("DROP TABLE IF EXISTS "+table);
        db.execSQL("CREATE TABLE " + DB_CHAT_TABLE + " (mess_id	INTEGER PRIMARY KEY AUTOINCREMENT, mess_to_id INTEGER, mess_from_id INTEGER, mess_text TEXT, mess_date VARCHAR);");
        return true;
    }
}
