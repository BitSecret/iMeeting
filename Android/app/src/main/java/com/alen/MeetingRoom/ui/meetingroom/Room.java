package com.alen.MeetingRoom.ui.meetingroom;

import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class Room {

    private static String rtag = "RoomTag";
    static final int AVAILABLE = 0;
    static final int RESERVED = 1;
    static final int OCCUPIED_BY_OTHERS = 2;
    static final int OUTOFSERVICE = 3;

    public int time;

    public Room(int time) {
        this.time = time;
    }
    private int roomNo;
    /**
     * 日期作为key，上面的四个变量作为value
     * 例：（0910，0）表示9月10日房间可用
     */
    private Map<Integer,Integer> roomDetail = new HashMap<>();

    public int getRoomNo() {
        int fistNumber = time/8+1;
        int lastNumber = time%8;
        if (lastNumber == 0) {
            fistNumber -= 1;
            lastNumber = 8;
        }
//        Log.d(rtag,fistNumber+"0"+lastNumber);
        return Integer.parseInt(fistNumber+"0"+lastNumber);
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public Map<Integer, Integer> getRoomDetail() {
        return roomDetail;
    }

    public void setRoomDetail(Map<Integer, Integer> roomDetail) {
        this.roomDetail = roomDetail;
    }


}
