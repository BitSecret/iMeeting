package com.alen.MeetingRoom.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alen.MeetingRoom.Bean.BookBean;
import com.alen.MeetingRoom.http.WorkServices;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import retrofit2.http.POST;
import retrofit2.http.Path;

public class WorkDatabase {

    private static final String DB_NAME = "work_db";
    private static final int DB_VERSION = 1;
    private Context context;
    private static WorkDatabase workDatabase;
    private SQLiteDatabase sqLiteDatabase;

    private WorkDatabase(Context context){
        WorkDataBaseOpenHelper openHelper = new WorkDataBaseOpenHelper(context,DB_NAME,null,DB_VERSION);
        sqLiteDatabase = openHelper.getWritableDatabase();
        this.context = context;
    }

    public synchronized static WorkDatabase getInstance(Context context){
        if (null == workDatabase) {
            workDatabase = new WorkDatabase(context);
        }
        return workDatabase;
    }

    public boolean saveBookingDatas(List<BookBean> bookBeans,String user_id){
        try {
            if (null != bookBeans && bookBeans.size() > 0 ){
                ContentValues contentValues = new ContentValues();
                Log.d("addbookTag","add book ");
                for (int i = 0;i < bookBeans.size();i++){
                    contentValues.put("book_id",bookBeans.get(i).getId());
                    contentValues.put("user_id",user_id);
                    contentValues.put("found_time",bookBeans.get(i).getFound_time());
                    contentValues.put("booking_date",bookBeans.get(i).getBooking_date());
                    contentValues.put("remark",bookBeans.get(i).getRemark());
                    contentValues.put("start_time",bookBeans.get(i).getStart_time());
                    contentValues.put("end_time",bookBeans.get(i).getEnd_time());
                    contentValues.put("room",bookBeans.get(i).getRoom());
                    sqLiteDatabase.insert("booking_table",null,contentValues);
                    contentValues.clear();
                }
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public List<BookBean> getAllBookingList(){
        List<BookBean> list = new ArrayList<>();
        String user_id = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("user_id","null");
        Cursor cursor = sqLiteDatabase.query("booking_table",null,"user_id like?",new String[]{user_id},null,null,null);
        BookBean bookBean = null;
        if (null == cursor){
            return list;
        }
        if (cursor.moveToFirst()){
            do {
                bookBean = new BookBean(
                        cursor.getInt(cursor.getColumnIndex("book_id")),
                        cursor.getString(cursor.getColumnIndex("found_time")),
                        cursor.getString(cursor.getColumnIndex("booking_date")),
                        cursor.getString(cursor.getColumnIndex("remark")),
                        cursor.getString(cursor.getColumnIndex("start_time")),
                        cursor.getString(cursor.getColumnIndex("end_time")),
                        cursor.getString(cursor.getColumnIndex("room"))
                );
                list.add(bookBean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<BookBean> getOneDayBookingData(String booking_date,String user_id){
        List<BookBean> list = new ArrayList<>();
        BookBean bookBean;
        Cursor cursor = sqLiteDatabase.query("booking_table",null,"booking_date like? and user_id like?",new String[]{booking_date,user_id},null,null,null);
        if ( null == cursor) {
            return list;
        }
        if (cursor.moveToFirst()){
            do {
                bookBean = new BookBean(
                        cursor.getInt(cursor.getColumnIndex("book_id")),
                        cursor.getString(cursor.getColumnIndex("found_time")),
                        cursor.getString(cursor.getColumnIndex("booking_date")),
                        cursor.getString(cursor.getColumnIndex("remark")),
                        cursor.getString(cursor.getColumnIndex("start_time")),
                        cursor.getString(cursor.getColumnIndex("end_time")),
                        cursor.getString(cursor.getColumnIndex("room"))
                );
                list.add(bookBean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean addABooking(BookBean bookBean,String user_id){
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("book_id",bookBean.getId());
            contentValues.put("user_id", user_id);
            contentValues.put("found_time",bookBean.getFound_time());
            contentValues.put("booking_date",bookBean.getBooking_date());
            contentValues.put("remark",bookBean.getRemark());
            contentValues.put("start_time",bookBean.getStart_time());
            contentValues.put("end_time", bookBean.getEnd_time());
            contentValues.put("room",bookBean.getRoom());
//            Log.d("getRoomTAg","Word database add booking "+((bookBean.getRoom())+""));
            sqLiteDatabase.insert("booking_table",null,contentValues);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteBooking(String book_id){
        try {
            sqLiteDatabase.delete("booking_table","book_id like ?",new String[]{book_id});
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public void deleteAllBooking(){
        sqLiteDatabase.execSQL("delete from booking_table;");
    }


}
