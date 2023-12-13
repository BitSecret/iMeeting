package com.alen.MeetingRoom.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WorkDataBaseOpenHelper extends SQLiteOpenHelper {

    public static final String create_table_booking = "create table booking_table(" +
            "id integer primary key autoincrement," +
            "book_id text unique,"+
            "user_id text,"+
            "found_time text," +
            "booking_date text," +
            "remark text," +
            "start_time text," +
            "end_time text," +
            "room text)";

    public WorkDataBaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("qweqwe","created database");
        db.execSQL(create_table_booking);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
