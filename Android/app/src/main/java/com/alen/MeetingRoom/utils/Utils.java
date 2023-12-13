package com.alen.MeetingRoom.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.alen.MeetingRoom.app.MyApplication;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.RequestBody;

public class Utils {

    public static Context getContext(){
        return MyApplication.app;
    }

    public static RequestBody getBody(Object o){
        return getBody("application/json; charset=utf-8", o);
    }

    public static RequestBody getBody(String type, Object o){
        return RequestBody.create(okhttp3.MediaType.parse(type), new Gson().toJson(o));
    }

    public static void selectorTime(Activity activity, Calendar setCalendar, final OnSelectorTime onSelectorTime) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(2020, 1, 1);
        new TimePickerBuilder(activity, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (onSelectorTime != null) {
                    String time = TimeUtils.millis2String(date.getTime());
                    onSelectorTime.onBack(time.substring(0, 10));
                }
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(startDate, endDate)
                .setDate(setCalendar == null ? Calendar.getInstance() : setCalendar)
                .setOutSideCancelable(true)
                .build().show();
    }

    public interface OnSelectorTime {

        void onBack(String date);
    }

    public static String changeDay(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);//增加一天
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getToDay() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cd = Calendar.getInstance();
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public static int XXX2XX(String number){
        int roomNumber = -1;
        int firstNumber = Integer.parseInt(number.split("0")[0]);
        int lastNumebr = Integer.parseInt(number.split("0")[1]);
        roomNumber = (firstNumber-1)*8 + lastNumebr;
        return roomNumber;
    }

    public static String XX2XXX(int number){
        String newNumber = null;
        // 103
        if (number < 8){
            newNumber = "10" + number%8;
        }else {
            if (number%8 != 0) {
                newNumber = number / 8 + 1 + "0" + number % 8;
            }else {
                newNumber = number / 8  + "08";
            }
        }
        return newNumber;
    }

    public static int getDaysBetween2Days(String first, String secind) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        //跨年的情况会出现问题哦
        //如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 1
//        Date fDate=sdf.parse("2019-03-08");
        Date fDate=sdf.parse(first);
//        Date oDate=sdf.parse("2016-03-08");
        Date oDate=sdf.parse(secind);
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        int days=day2-day1;
//        System.out.print(days);
        return days;
    }

}
