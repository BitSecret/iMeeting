package com.alen.MeetingRoom.utils;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//界面工具类
public class UIUtils {

    //设置透明状态栏
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static void setStatusBar(Window window, int barFont) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            switch (barFont) {
                case WHITE:
                    option &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    break;
                case BLACK:
                    option |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    break;
                default:
            }
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 0：可预定
     * 1：不可预定
     * @return
     */
    public static Map<Integer, Integer> generateRandomRoomDetail(int isbooked,String start_time, String end_time) {
        Map<Integer, Integer> roomDetail = new HashMap<>();
        roomDetail.put(1, 0);
        roomDetail.put(2, 0);
        roomDetail.put(3, 0);
        roomDetail.put(4, 0);
        roomDetail.put(5, 0);
        roomDetail.put(6, 0);
        roomDetail.put(7, 0);
        roomDetail.put(8, 0);
        roomDetail.put(9, 0);
        roomDetail.put(10, 0);
        roomDetail.put(11, 0);
        roomDetail.put(12, 0);
        roomDetail.put(13, 0);
        roomDetail.put(14, 0);
        roomDetail.put(15, 0);
        int startHour = Integer.parseInt(start_time.split(":")[0]);
        int endHour = Integer.parseInt(end_time.split(":")[0]);
        if (startHour != endHour) {
//            if (!end_time.split(":")[1].equals("00") && Integer.parseInt(end_time.split(":")[1]) < 45) {
//            Log.d("sdcasc",Integer.parseInt(end_time.split(":")[1])+" 是 ");
            if (Integer.parseInt(end_time.split(":")[1]) > 0) {
                endHour += 1;
            }else {
                endHour-=1;
            }
        }
        if (isbooked == 0){
            return roomDetail;
        }
        startHour -= 7;
        endHour -= 7;

        for (int i = startHour; i <= endHour; i++){
            roomDetail.put(i,1);
        }
        return roomDetail;

    }

    public static Map<Integer, Integer> AddRandomRoomDetail(int isbooked, String start_time, String end_time, Map<Integer, Integer> roomDetail) {
//        Map<Integer, Integer> mRoomDetail = new HashMap<>();
//        mRoomDetail = roomDetail;
        int startHour = Integer.parseInt(start_time.split(":")[0]);
        int endHour = Integer.parseInt(end_time.split(":")[0]);
        if (startHour != endHour) {
            if (Integer.parseInt(end_time.split(":")[1]) > 0) {
                endHour += 1;
            }else {
                endHour-=1;
            }
        }
        if (isbooked == 0){
            return roomDetail;
        }

        startHour -= 7;
        endHour -= 7;

        for (int i = startHour; i <= endHour; i++){

            if (isbooked == 2){
                roomDetail.put(i,2);
            }else {
                roomDetail.put(i,1);
            }
        }
        return roomDetail;
    }
}
