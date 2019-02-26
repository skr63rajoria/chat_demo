package com.chat.shubham.chatdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CDAC on 1/4/2017.
 */
public class ChatDataBaseHelper extends SQLiteOpenHelper {
    Context context;

    public ChatDataBaseHelper(Context context) {
        super(context, "chatdb", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user (username text)");
        db.execSQL("create table friend (fname text primary key, firebasekey text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists friend");
        onCreate(db);

    }

    boolean adduser(String user)
    {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username",user);

        long i = database.insert("user",null,cv);
        if(i == -1)
        {
            return false;
        }
        else {
            return true;
        }
    }


    boolean addFreinds(String name, String key)
    {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("fname",name);
        cv.put("firebasekey",key);
        long i = database.insert("friend",null,cv);
        if(i != -1)
        {
            return false;
        }
        else {
            return true;
        }
    }


    Cursor getFriends()
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from friend",null);
        return cursor;
    }

    Cursor getUser()
    {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from user",null);
        return cursor;
    }


    void deleteUser(String user)
    {
        SQLiteDatabase database = getWritableDatabase();
        //sqLiteDatabase.delete("friends","person=?",new String[]{name});
        database.delete("user","username=?",new String[]{user});
    }

    void deleteFriend(String name)
    {
        SQLiteDatabase database = getWritableDatabase();
        //sqLiteDatabase.delete("friends","person=?",new String[]{name});
        database.delete("friend","fname=?",new String[]{name});
    }
}
