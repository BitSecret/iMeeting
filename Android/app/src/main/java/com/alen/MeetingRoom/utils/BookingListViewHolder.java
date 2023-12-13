package com.alen.MeetingRoom.utils;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alen.MeetingRoom.R;
import com.chad.library.adapter.base.BaseViewHolder;

public class BookingListViewHolder extends BaseViewHolder {

    public TextView tv_meeting_room_name;  // 会议室:106
    public TextView tv_meeting_room_time;  // 时间:2019-02-14 16:20-16:30
    public TextView tv_meeting_room_remarks;  // 备注:106
    public Button btn_delete_order;

    public BookingListViewHolder(View view) {
        super(view);
        tv_meeting_room_name = view.findViewById(R.id.tv_meeting_room_name);
        tv_meeting_room_time = view.findViewById(R.id.tv_meeting_room_time);
        tv_meeting_room_remarks = view.findViewById(R.id.tv_meeting_room_remarks);
        btn_delete_order = view.findViewById(R.id.btn_delete_order);
    }
}
