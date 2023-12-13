package com.alen.MeetingRoom.Bean;

public class BookBean {


    /**
     * id : 25
     * found_time : 2019-03-04T20:07:57.890308
     * booking_date : 2019-03-04
     * remark : test
     * start_time : 12:00:00
     * end_time : 12:15:00
     * room : 1
     */

    private int id;
    private String found_time;
    private String booking_date;
    private String remark;
    private String start_time;
    private String end_time;
    private String room;

    public BookBean(int id, String found_time, String booking_date, String remark, String start_time, String end_time, String room) {
        this.id = id;
        this.found_time = found_time;
        this.booking_date = booking_date;
        this.remark = remark;
        this.start_time = start_time;
        this.end_time = end_time;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFound_time() {
        return found_time;
    }

    public void setFound_time(String found_time) {
        this.found_time = found_time;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room+"";
    }
}
