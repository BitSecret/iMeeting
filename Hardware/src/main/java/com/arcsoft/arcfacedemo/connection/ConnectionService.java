package com.arcsoft.arcfacedemo.connection;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.arcsoft.arcfacedemo.info.BookingInfo;
import com.arcsoft.arcfacedemo.info.UserInfo;
import com.arcsoft.face.FaceFeature;

public class ConnectionService extends Service {
    UserInfo userInfoHead;
    UserInfo userInfoNow;
    BookingInfo bookingInfoHead;
    BookingInfo bookingInfoNow;
    boolean isOver;
    boolean readingComplete;
    Bitmap picture;
    FaceFeature faceFeature;


    @Override
    public void onCreate() {    //连接数据库操作
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public UserInfo getUserInfo(final String data, int bookingIdOrUserIdOrAll) {   //type=0  bookingId; type=1 userId
        System.out.println("开始查询人员信息");
        userInfoHead = null;
        userInfoNow = null;
        isOver = false;
        if(bookingIdOrUserIdOrAll == 1) {    /*通过UserId查询*/
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    DBHelper dbHelper = new DBHelper();
                    String sql = "select * from UserLogin_user where id=" + data;
                    ResultSet resultSet = dbHelper.getSelectResult(sql);
                    try{
                        while(resultSet.next()){
                            if(resultSet.getString("id").equals(data)){
                                System.out.println(
                                        resultSet.getString("id") +
                                                "\t" + resultSet.getString("name") +
                                                "\t" + resultSet.getString("password") +
                                                "\t" + resultSet.getString("nick_name") +
                                                "\t" + resultSet.getString("face_data_file") +
                                                "\t" + resultSet.getString("picture") +
                                                "\t" + resultSet.getString("email") +
                                                "\t" + resultSet.getString("phone_number"));
                                if(userInfoHead == null) {
                                    userInfoHead = new UserInfo();
                                    userInfoHead.last = null;
                                    userInfoHead.next = null;
                                    userInfoHead = new UserInfo();
                                    userInfoHead.userId = resultSet.getString("id");
                                    userInfoHead.userName = resultSet.getString("name");
                                    userInfoHead.password = resultSet.getString("password");
                                    userInfoHead.nickName = resultSet.getString("nick_name");
                                    userInfoHead.faceFeature = getFaceFeatureByAddress(resultSet.getString("face_data_file"));
                                    userInfoHead.picture = getBitmapByAddress(resultSet.getString("picture"));
                                    userInfoHead.email = resultSet.getString("email");
                                    userInfoHead.phone = resultSet.getString("phone_number");
                                    userInfoHead.next = null;
                                    userInfoHead.last = null;
                                    userInfoNow = userInfoHead;
                                }
                                else{
                                    userInfoNow.next = new UserInfo();
                                    userInfoNow.next.next = null;
                                    userInfoNow.next.last = userInfoNow;
                                    userInfoNow = userInfoNow.next;
                                    userInfoNow.userId = resultSet.getString("id");
                                    userInfoNow.userName = resultSet.getString("name");
                                    userInfoNow.password = resultSet.getString("password");
                                    userInfoNow.nickName = resultSet.getString("nick_name");
                                    userInfoNow.faceFeature = getFaceFeatureByAddress(resultSet.getString("face_data_file"));
                                    userInfoNow.picture = getBitmapByAddress(resultSet.getString("picture"));
                                    userInfoNow.email = resultSet.getString("email");
                                    userInfoNow.phone = resultSet.getString("phone_number");
                                }
                                break;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    isOver = true;
                }
            });
            thread.start();
        }
        else if (bookingIdOrUserIdOrAll == 0){           /*通过bookingId查询*/
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    DBHelper dbHelper = new DBHelper();
                    String sql = "select * from UserLogin_meeting where order_id=" + data;
                    ResultSet resultSet = dbHelper.getSelectResult(sql);
                    try{
                        while(resultSet.next()){
                            if(resultSet.getString("order_id").equals(data)){
                                System.out.println(
                                        resultSet.getString("id") +
                                                "\t" + resultSet.getString("guest_id") +
                                                "\t" + resultSet.getString("host_id") +
                                                "\t" + resultSet.getString("order_id") +
                                                "\t" + resultSet.getString("auth") +
                                                "\t" + resultSet.getString("join_remark"));
                                if(userInfoHead == null){
                                    userInfoHead = new UserInfo();
                                    userInfoHead.last = null;
                                    userInfoHead.next = null;
                                    userInfoHead.userId = resultSet.getString("guest_id");
                                    userInfoNow = userInfoHead;
                                }
                                else{
                                    userInfoNow.next = new UserInfo();
                                    userInfoNow.next.next = null;
                                    userInfoNow.next.last = userInfoNow;
                                    userInfoNow.next.userId = resultSet.getString("guest_id");
                                    userInfoNow = userInfoNow.next;
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    bookingInfoNow = bookingInfoHead;
                    while(bookingInfoNow != null){
                        sql = "select * from UserLogin_user where id=" + bookingInfoNow.userId;
                        resultSet = dbHelper.getSelectResult(sql);
                        try{
                            while(resultSet.next()){
                                if(resultSet.getString("id").equals(bookingInfoNow.userId)){
                                    System.out.println(
                                            resultSet.getString("id") +
                                                    "\t" + resultSet.getString("name") +
                                                    "\t" + resultSet.getString("password") +
                                                    "\t" + resultSet.getString("nick_name") +
                                                    "\t" + resultSet.getString("face_data_file") +
                                                    "\t" + resultSet.getString("picture") +
                                                    "\t" + resultSet.getString("email") +
                                                    "\t" + resultSet.getString("phone_number"));

                                    userInfoNow.userId = resultSet.getString("id");
                                    userInfoNow.userName = resultSet.getString("name");
                                    userInfoNow.password = resultSet.getString("password");
                                    userInfoNow.nickName = resultSet.getString("nick_name");
                                    userInfoNow.faceFeature = getFaceFeatureByAddress(resultSet.getString("face_data_file"));
                                    userInfoNow.picture = getBitmapByAddress(resultSet.getString("picture"));
                                    userInfoNow.email = resultSet.getString("email");
                                    userInfoNow.phone = resultSet.getString("phone_number");
                                    userInfoNow.next = null;
                                    userInfoNow.last = null;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        bookingInfoNow = bookingInfoNow.next;
                    }
                    isOver = true;
                }
            });
            thread.start();
        }
        else{      //查询所有
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    DBHelper dbHelper = new DBHelper();
                    String sql = "select * from UserLogin_user";
                    ResultSet resultSet = dbHelper.getSelectResult(sql);
                    try{
                        while(resultSet.next()){
                            /*System.out.println(
                                    resultSet.getString("id") +
                                            "\t" + resultSet.getString("name") +
                                            "\t" + resultSet.getString("password") +
                                            "\t" + resultSet.getString("nick_name") +
                                            "\t" + resultSet.getString("face_data_file") +
                                            "\t" + resultSet.getString("picture") +
                                            "\t" + resultSet.getString("email") +
                                            "\t" + resultSet.getString("phone_number"));*/
                            if(userInfoHead == null) {
                                userInfoHead = new UserInfo();
                                userInfoHead.last = null;
                                userInfoHead.next = null;
                                userInfoHead = new UserInfo();
                                userInfoHead.userId = resultSet.getString("id");
                                userInfoHead.userName = resultSet.getString("name");
                                //userInfoHead.password = resultSet.getString("password");
                                //userInfoHead.nickName = resultSet.getString("nick_name");
                                userInfoHead.faceFeature = getFaceFeatureByAddress(resultSet.getString("face_data_file"));
                                //userInfoHead.picture = getBitmapByAddress(resultSet.getString("picture"));
                                //userInfoHead.email = resultSet.getString("email");
                                //userInfoHead.phone = resultSet.getString("phone_number");
                                userInfoHead.next = null;
                                userInfoHead.last = null;
                                userInfoNow = userInfoHead;
                            }
                            else{
                                userInfoNow.next = new UserInfo();
                                userInfoNow.next.next = null;
                                userInfoNow.next.last = userInfoNow;
                                userInfoNow = userInfoNow.next;
                                userInfoNow.userId = resultSet.getString("id");
                                userInfoNow.userName = resultSet.getString("name");
                                //userInfoNow.password = resultSet.getString("password");
                                //userInfoNow.nickName = resultSet.getString("nick_name");
                                userInfoNow.faceFeature = getFaceFeatureByAddress(resultSet.getString("face_data_file"));
                                //userInfoNow.picture = getBitmapByAddress(resultSet.getString("picture"));
                                //userInfoNow.email = resultSet.getString("email");
                                //userInfoNow.phone = resultSet.getString("phone_number");
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    isOver = true;
                }
            });
            thread.start();
        }

        while(true){    //循环等待查询线程结束
            if(isOver){
                break;}
            else{
                try { Thread.sleep(250);
                } catch (InterruptedException ie){ ie.printStackTrace(); }}
        }

        System.out.println("查询人员信息结束");
        return userInfoHead;
    }

    public BookingInfo getBookingInfo(final String data, int meetingRoomOrUserId){   //type=0  meetingRoom; type=1 userId
        System.out.println("开始查询订单信息");
        bookingInfoHead  = null;
        bookingInfoNow = null;
        isOver = false;
        if(meetingRoomOrUserId == 0){    //Search BookingInfo By roomId
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    DBHelper dbHelper = new DBHelper();
                    String sql = "select * from mainsystem_order where room_id=" + data;    //查库mainsystem_order
                    ResultSet resultSet = dbHelper.getSelectResult(sql);
                    try{
                        while(resultSet.next()){
                            /*
                            System.out.println(
                                    resultSet.getString("id") +    //订单号
                                            "\t" + resultSet.getString("found_time") +    //下单日期
                                            "\t" + resultSet.getDate("booking_date") +    //预定日期
                                            "\t" + resultSet.getString("remark") +    //标题
                                             "\t" + resultSet.getTime("start_time") +    //开始时间
                                            "\t" + resultSet.getTime("end_time") +    //结束时间
                                            "\t" + resultSet.getString("room_id"));    //room_id*/

                            if(bookingInfoHead == null) {
                                bookingInfoHead = new BookingInfo();
                                bookingInfoHead.bookingId = resultSet.getString("id");
                                bookingInfoHead.foundingTime = resultSet.getTimestamp("found_time");
                                bookingInfoHead.bookingDate = resultSet.getDate("booking_date");
                                bookingInfoHead.meetingName = resultSet.getString("remark");
                                bookingInfoHead.startTime = resultSet.getTime("start_time");
                                bookingInfoHead.endTime = resultSet.getTime("end_time");
                                bookingInfoHead.meetingNote = "";
                                bookingInfoHead.startTimestamp = Timestamp.valueOf(bookingInfoHead.bookingDate.toString() + " " + bookingInfoHead.startTime.toString());
                                bookingInfoHead.endTimestamp = Timestamp.valueOf(bookingInfoHead.bookingDate.toString() + " " + bookingInfoHead.endTime.toString());
                                bookingInfoHead.roomId = resultSet.getString("room_id");
                                bookingInfoHead.last = null;
                                bookingInfoHead.next = null;
                                bookingInfoNow = bookingInfoHead;
                            }
                            else {
                                bookingInfoNow.next = new BookingInfo();
                                bookingInfoNow.next.last = bookingInfoNow;
                                bookingInfoNow.next.next = null;
                                bookingInfoNow = bookingInfoNow.next;
                                bookingInfoNow.bookingId = resultSet.getString("id");
                                bookingInfoNow.foundingTime = resultSet.getTimestamp("found_time");
                                bookingInfoNow.bookingDate = resultSet.getDate("booking_date");
                                bookingInfoNow.meetingName = resultSet.getString("remark");
                                bookingInfoNow.startTime = resultSet.getTime("start_time");
                                bookingInfoNow.endTime = resultSet.getTime("end_time");
                                bookingInfoNow.startTimestamp = Timestamp.valueOf(bookingInfoNow.bookingDate.toString() + " " + bookingInfoNow.startTime.toString());
                                bookingInfoNow.endTimestamp = Timestamp.valueOf(bookingInfoNow.bookingDate.toString() + " " + bookingInfoNow.endTime.toString());
                                bookingInfoNow.roomId = resultSet.getString("room_id");
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (bookingInfoHead != null){
                        bookingInfoNow = bookingInfoHead;    //查库UserLogin_user_order
                        while(bookingInfoNow != null){
                            sql = "select * from UserLogin_user_order where order_id=" + bookingInfoNow.bookingId;
                            resultSet = dbHelper.getSelectResult(sql);
                            try {
                                if (resultSet.next()) {
                                    /*
                                    System.out.println(
                                            resultSet.getString("id") +
                                                    "\t" + resultSet.getString("order_id") +
                                                    "\t" + resultSet.getString("user_id"));
*/
                                    bookingInfoNow.userId = resultSet.getString("user_id");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            bookingInfoNow = bookingInfoNow.next;    //查找下一个
                        }

                        bookingInfoNow = bookingInfoHead;    //查库UserLogin_user_order
                        while(bookingInfoNow != null){
                            sql = "select name from UserLogin_user where id=" + bookingInfoNow.userId;
                            resultSet = dbHelper.getSelectResult(sql);
                            try {
                                if (resultSet.next()) {
                                    //System.out.println(resultSet.getString("name"));
                                    bookingInfoNow.userName = resultSet.getString("name");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            bookingInfoNow = bookingInfoNow.next;    //查找下一个
                        }

                        bookingInfoNow = bookingInfoHead;    //查库mainsystem_meetingroom
                        while(bookingInfoNow != null){
                            sql = "select * from mainsystem_meetingroom where id=" + bookingInfoNow.roomId;
                            resultSet = dbHelper.getSelectResult(sql);
                            try {
                                if (resultSet.next()) {
                                   /* System.out.println(
                                            resultSet.getString("id") +    //房间id
                                                    "\t" + resultSet.getString("room_number") +    //房间号
                                                    "\t" + resultSet.getString("room_type") +    //房间类型
                                                    "\t" + resultSet.getString("content") +    //容纳人数
                                                    "\t" + resultSet.getString("img"));    //图片地址*/
                                    bookingInfoNow.roomNumber = resultSet.getString("room_number");
                                    //bookingInfoNow.roomType = resultSet.getString("room_type");
                                    bookingInfoNow.content = resultSet.getInt("content");
                                    //bookingInfoNow.roomImage =  getBitmapByAddress(resultSet.getString("img"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            bookingInfoNow = bookingInfoNow.next;    //查找下一个
                        }
                    }
                    isOver = true;
                }
            });
            thread.start();
        }
        else {        //Search BookingInfo By userId
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    DBHelper dbHelper = new DBHelper();
                    String sql = "select * from UserLogin_user_order where user_id=" + data;    //UserLogin_user_order
                    ResultSet resultSet = dbHelper.getSelectResult(sql);
                    try{
                        while(resultSet.next()){
                            /*System.out.println(
                                    resultSet.getString("order_id") +    //订单号
                                            "\t" + resultSet.getString("user_id"));*/
                            if(bookingInfoHead == null) {
                                bookingInfoHead = new BookingInfo();
                                bookingInfoHead.bookingId = resultSet.getString("order_id");
                                bookingInfoHead.userId = resultSet.getString("user_id");
                                bookingInfoHead.last = null;
                                bookingInfoHead.next = null;
                                bookingInfoNow = bookingInfoHead;
                            }
                            else {
                                bookingInfoNow.next = new BookingInfo();
                                bookingInfoNow.next.last = bookingInfoNow;
                                bookingInfoNow.next.next = null;
                                bookingInfoNow = bookingInfoNow.next;
                                bookingInfoNow.bookingId = resultSet.getString("order_id");
                                bookingInfoNow.userId = resultSet.getString("user_id");
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(bookingInfoHead != null){
                        bookingInfoNow = bookingInfoHead;    //查库UserLogin_user
                        sql = "select * from UserLogin_user where id=" + bookingInfoNow.userId;
                        String name = "";
                        resultSet = dbHelper.getSelectResult(sql);
                        try {
                            if (resultSet.next()) {
                                name = resultSet.getString("name");
                                System.out.println(name);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        while(bookingInfoNow != null){
                            bookingInfoNow.userName = name;
                            bookingInfoNow = bookingInfoNow.next;    //查找下一个
                        }

                        bookingInfoNow = bookingInfoHead;    //查库mainsystem_order
                        while(bookingInfoNow != null){
                            sql = "select * from mainsystem_order where id=" + bookingInfoNow.bookingId;
                            resultSet = dbHelper.getSelectResult(sql);
                            try {
                                if (resultSet.next()) {
                                    /*System.out.println(
                                            resultSet.getString("id") +    //订单号
                                                    "\t" + resultSet.getString("found_time") +    //下单日期
                                                    "\t" + resultSet.getDate("booking_date") +    //预定日期
                                                    "\t" + resultSet.getString("remark") +    //标题
                                                    "\t" + resultSet.getTime("start_time") +    //开始时间
                                                    "\t" + resultSet.getTime("end_time") +    //结束时间
                                                    "\t" + resultSet.getString("room_id"));    //room_id
*/
                                    bookingInfoNow.bookingId = resultSet.getString("id");
                                    bookingInfoNow.meetingName = resultSet.getString("remark");
                                    bookingInfoNow.foundingTime = resultSet.getTimestamp("found_time");
                                    bookingInfoNow.bookingDate = resultSet.getDate("booking_date");
                                    bookingInfoNow.startTime = resultSet.getTime("start_time");
                                    bookingInfoNow.endTime = resultSet.getTime("end_time");
                                    bookingInfoNow.startTimestamp = Timestamp.valueOf(bookingInfoNow.bookingDate.toString() + " " + bookingInfoNow.startTime.toString());
                                    bookingInfoNow.endTimestamp = Timestamp.valueOf(bookingInfoNow.bookingDate.toString() + " " + bookingInfoNow.endTime.toString());
                                    bookingInfoNow.meetingNote = "";
                                    bookingInfoNow.meetingState = "";
                                    bookingInfoNow.roomId = resultSet.getString("room_id");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            bookingInfoNow = bookingInfoNow.next;    //查找下一个
                        }

                        bookingInfoNow = bookingInfoHead;    //查库mainsystem_meetingroom
                        while(bookingInfoNow != null){
                            sql = "select * from mainsystem_meetingroom where id=" + bookingInfoNow.roomId;
                            resultSet = dbHelper.getSelectResult(sql);
                            try {
                                if (resultSet.next()) {
                                    /*
                                    System.out.println(
                                            resultSet.getString("id") +    //房间id
                                                    "\t" + resultSet.getString("room_number") +    //房间号
                                                    "\t" + resultSet.getString("room_type") +    //房间类型
                                                    "\t" + resultSet.getString("content") +    //容纳人数
                                                    "\t" + resultSet.getString("img"));    //图片地址*/
                                    bookingInfoNow.roomNumber = resultSet.getString("room_number");
                                    bookingInfoNow.roomType = resultSet.getString("room_type");
                                    bookingInfoNow.content = resultSet.getInt("content");
                                    bookingInfoNow.roomNumber = resultSet.getString("room_number");
                                    //bookingInfoNow.roomImage =  getBitmapByAddress(resultSet.getString("img"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            bookingInfoNow = bookingInfoNow.next;    //查找下一个
                        }
                    }
                    isOver = true;
                }
            });
            thread.start();
        }
        while(true){//循环等待查询线程结束
            if(isOver){
                break;}
            else{
                try { Thread.sleep(250);
                } catch (InterruptedException ie){ ie.printStackTrace(); }}
        }

        System.out.println("预订信息输出完毕");
        return bookingInfoHead;
    }


    public FaceFeature getFaceFeatureByAddress(String address){
        if (address.equals("None")){
            return null;
        }
        final String str_url = "http://47.106.81.165:8000/usermedia/" + address;
        System.out.println(str_url);
        readingComplete = false;
        faceFeature = null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(str_url);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5 * 1000);
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte tmp [] = new byte[1024];
                    int i ;
                    while((i=inputStream.read(tmp, 0, 1024))>0){
                        baos.write(tmp, 0, i);
                    }
                    byte imgs[] = baos.toByteArray();
                    if (new String(imgs).equals("None")){
                        faceFeature = null;
                    }
                    else{
                        faceFeature = new FaceFeature();
                        faceFeature.setFeatureData(imgs);
                        System.out.println(new String(imgs));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                readingComplete = true;
            }
        });
        thread.start();

        while(true){//循环等待读取线程结束
            if(readingComplete){
                break;}
            else{
                try { Thread.sleep(100);
                } catch (InterruptedException ie){ ie.printStackTrace(); }}
        }

        return faceFeature;
    }

    public Bitmap getBitmapByAddress(String address){
        if (address.equals("None")){
            return null;
        }
        final String str_url = "http://47.106.81.165:8000/usermedia/" + address;
        readingComplete = false;
        picture = null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(str_url);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5 * 1000);
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte tmp [] = new byte[1024];
                    int i ;
                    while((i=inputStream.read(tmp, 0, 1024))>0){
                        baos.write(tmp, 0, i);
                    }
                    byte imgs[] = baos.toByteArray();
                    picture = BitmapFactory.decodeByteArray(imgs, 0, imgs.length);
                }catch (Exception e){
                    e.printStackTrace();
                }
                readingComplete = true;
            }
        });
        thread.start();

        while(true){//循环等待读取线程结束
            if(readingComplete){
                break;}
            else{
                try { Thread.sleep(100);
                } catch (InterruptedException ie){ ie.printStackTrace(); }}
        }

        return picture;
    }
}

