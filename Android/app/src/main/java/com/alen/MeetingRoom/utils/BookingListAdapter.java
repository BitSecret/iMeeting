package com.alen.MeetingRoom.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.alen.MeetingRoom.Bean.BookBean;
import com.alen.MeetingRoom.R;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

public class BookingListAdapter extends BaseQuickAdapter<BookBean,BookingListViewHolder> {
    private Context context;
    public BookingListAdapter(Context context,int layoutResId, @Nullable List<BookBean> data) {
        super(layoutResId, data);
        initData(data);
    }

    private void initData(List<BookBean> data) {

    }

    @Override
    protected void convert(BookingListViewHolder helper, BookBean item) {
        if (null == item){
            return;
        }
        String roomText = item.getRoom();
        helper.addOnClickListener(R.id.btn_delete_order);
        helper.tv_meeting_room_name.setText("会议室:" + roomText);  // 会议室:106;
        try {
            helper.tv_meeting_room_time.setText("时间:" + item.getBooking_date()+" "+item.getStart_time().substring(0,5)+"-"+item.getEnd_time().substring(0,5));  // 时间:2019-02-14 16:20-16:30
        }catch (IndexOutOfBoundsException e){
            helper.tv_meeting_room_time.setText("时间:" + item.getBooking_date()+" "+item.getStart_time()+"-"+item.getEnd_time());  // 时间:2019-02-14 16:20-16:30

        }
        if (item.getRemark().equals("") ) {
            helper.tv_meeting_room_remarks.setText("备注: 无");  // 备注:106
        }else {
            helper.tv_meeting_room_remarks.setText("备注:" + item.getRemark());  // 备注:106
        }
    }

}
