package com.arcsoft.arcfacedemo.info;

import android.graphics.Bitmap;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class BookingInfo {
    public String roomId = "";
    public String roomNumber = "";
    public String roomType = "";
    public int content = 0;
    public Bitmap roomImage = null;

    public String bookingId = "";
    public String meetingName = "";
    public Timestamp foundingTime = null;    //预定时间
    public Date bookingDate = null;    //开会日期
    public Time startTime = null;    //开会时间
    public Timestamp startTimestamp = null;
    public Time endTime = null;
    public Timestamp endTimestamp = null;
    public String meetingState = "";
    public String meetingNote = "";
    public BookingInfo last;
    public BookingInfo next;

    public String userId = "";
    public String userName = "";
}
