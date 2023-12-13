package com.alen.MeetingRoom.ui.meetingroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alen.MeetingRoom.Bean.BookBean;
import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.broadcast.BookingListChangedReceiver;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.interfaces.DeleteBookingListener;
import com.alen.MeetingRoom.utils.UIUtils;
import com.alen.MeetingRoom.utils.Utils;
import com.alen.MeetingRoom.utils.WorkDatabase;
import com.alen.MeetingRoom.widget.TitleBarInfilater;
import com.blankj.utilcode.util.BarUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import sysu.zyb.panellistlibrary.PanelListLayout;
import sysu.zyb.panellistlibrary.Utility;

public class MeetingRoomActivity extends BaseActivity{

    @BindView(R.id.meeting_room_list)
    PanelListLayout meeting_room_list;
    @BindView(R.id.meeting_room_content)
    ListView meeting_room_content;
    @BindView(R.id.tv_set_date)
    TextView tv_set_date;

    private MeetingRoomAdapter adapter;
    private SharedPreferences.Editor userInfoEditor;
    private String booking_data;
    private String remark;
    private List<BookBean> bookBeanList;
    private List<Room> contentList = new ArrayList<>();

    private List<String> rowDataList, columnDataList;
    private int currentDate = 0;

    @Override
    public int getLayout() {
        return R.layout.activity_meeting_room;
    }

    @OnClick({R.id.tv_add_day, R.id.tv_reduce_day, R.id.tv_set_date})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_add_day:
                tv_set_date.setText(Utils.changeDay(tv_set_date.getText().toString(), 1));
                adapter.setBooking_date(tv_set_date.getText().toString());
                userInfoEditor.putString("selected_date",tv_set_date.getText().toString());
                changeData(tv_set_date.getText().toString());
//                bookBeanList = WorkDatabase.getInstance(this).getOneDayBookingData(tv_set_date.getText().toString());
//                for (BookBean bookBean : bookBeanList){
//                    Log.d("bookBean_tag",bookBean.getId() + " , " + bookBean.getRoom());
//                }
                currentDate += 1;
                break;
            case R.id.tv_reduce_day:
                tv_set_date.setText(Utils.changeDay(tv_set_date.getText().toString(), -1));
                adapter.setBooking_date(tv_set_date.getText().toString());
                userInfoEditor.putString("selected_date",tv_set_date.getText().toString());
                changeData(tv_set_date.getText().toString());
//                bookBeanList = WorkDatabase.getInstance(this).getOneDayBookingData(tv_set_date.getText().toString());
//                for (BookBean bookBean : bookBeanList){
//                    Log.d("bookBean_tag",bookBean.getId() + " , " + bookBean.getRoom());
//                }
                currentDate -= 1;
                break;
            case R.id.tv_set_date:
                Utils.selectorTime(mContext, null, new Utils.OnSelectorTime() {
                    @Override
                    public void onBack(String date) {
                        tv_set_date.setText(date);
                        adapter.setBooking_date(date);
                        userInfoEditor.putString("selected_date",tv_set_date.getText().toString());
                        changeData(tv_set_date.getText().toString());

//                        bookBeanList = WorkDatabase.getInstance(MeetingRoomActivity.this).getOneDayBookingData(tv_set_date.getText().toString());
//                        for (BookBean bookBean : bookBeanList){
//                            Log.d("bookBean_tag",bookBean.getId() + " , " + bookBean.getRoom());
//                        }
                        try {
                            currentDate = Utils.getDaysBetween2Days(Utils.getToDay(),tv_set_date.getText().toString());
//                            Log.d("asdadf","today is " + Utils.getToDay() + ", " + ", target day is " + tv_set_date.getText().toString() + ", cur is " + currentDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
        }
    }

    @Override
    public void init(Bundle savedInstanceState) {
//        BarUtils.addMarginTopEqualStatusBarHeight(meeting_room_list);
        refreshData();
        TitleBarInfilater.form(mContext).setTitleText("预订").setElevation(5);
        tv_set_date.setText(Utils.getToDay());
        bookBeanList = WorkDatabase.getInstance(this).getOneDayBookingData(Utils.getToDay(),getSharedPreferences("UserInfo",MODE_PRIVATE).getString("user_id","null"));
        initRowDataList();
        initContentDataList();
        initColumnDataList();

        userInfoEditor = getSharedPreferences("UserInfo",MODE_PRIVATE).edit();
        initAdapter();
        meeting_room_list.setAdapter(adapter);
        //        for (BookBean bookBean : bookBeanList){
//            Log.d("bookBean_tag",bookBean.getId() + " , " + bookBean.getRoom());
//        }
        adapter.setBooking_date(Utils.getToDay());

    }

    private void refreshData() {
        WorkServices.getInstance(this).getUserOders(getSharedPreferences("UserInfo",MODE_PRIVATE).getString("name","null"));
    }

    private void initAdapter(){
        adapter = new MeetingRoomAdapter(mContext, meeting_room_list,meeting_room_content,contentList,R.layout.item_room);
        adapter.setSwipeRefreshEnabled(false);
        // 第一行
        adapter.setRowDataList(rowDataList);
        adapter.setRowColor("#ffffff");
        booking_data = (String) tv_set_date.getText();
        userInfoEditor.putString("selected_date",tv_set_date.getText().toString());
        // 第一列
        adapter.setColumnDataList(columnDataList);
        adapter.setColumnColor("#ffffff");
        adapter.setTitle("##");// optional
        adapter.setTitleColor("#ffffff");
        adapter.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendToast("Refresh");
            }
        });
//        meeting_room_list

    }

    public class CustomRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            // you can do sth here, for example: make a toast:
            sendToast("custom SwipeRefresh listener");
            // don`t forget to call this
            adapter.getSwipeRefreshLayout().setRefreshing(false);
        }
    }

    /**
     * 生成一份横向表头的内容
     * 8：00 - 22：00  15
     * @return List<String>
     */
    private void initRowDataList() {
        rowDataList = new ArrayList<>();
        for (int i = 8; i < 23; i++) {
            rowDataList.add(i + ":00");
        }
    }

    /**
     * 生成一份纵向表头的内容
     *
     * @return List<String>
     */
    private void initColumnDataList() {
        columnDataList = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                columnDataList.add(i + "0" + j);
            }
        }
    }

    /**
     * 初始化content数据
     */
    private void initContentDataList() {
        Room room = null;
        for (int i = 1; i <= 64; i++) {
            // 设置每一行的格子的详情
            room = new Room(i);
            room.setRoomDetail(UIUtils.generateRandomRoomDetail(0,"0:0","0:0"));
            contentList.add(room);
        }
        if (null == bookBeanList){
            return;
        }

        for (BookBean bookBean: bookBeanList){
//            contentList.get(bookBean.getRoom()-1).setRoomDetail(UIUtils.generateRandomRoomDetail(1,bookBean.getStart_time(),bookBean.getEnd_time()));
            if (tv_set_date.getText().toString().equals(bookBean.getBooking_date())){
//                contentList.get(bookBean.getRoom()-1).setRoomDetail(
                Log.d("getRoomTAg",((bookBean.getRoom())+""));
                contentList.get(Utils.XXX2XX((bookBean.getRoom()))-1)
                        .setRoomDetail(
                                UIUtils.AddRandomRoomDetail(
                                    1,
                                    bookBean.getStart_time(),
                                    bookBean.getEnd_time(),
//                                contentList.get((bookBean.getRoom()-1)).getRoomDetail()
                                    contentList.get(Utils.XXX2XX(bookBean.getRoom())-1).getRoomDetail()
                                )
                        );
            }
        }
        List<BookBean> newBookBeans = WorkDatabase.getInstance(this).getOneDayBookingData(tv_set_date.getText().toString(),"-1");
        for (BookBean bookBean : newBookBeans){
            if (tv_set_date.getText().toString().equals(bookBean.getBooking_date())){
                Log.d("getRoomTAg",((bookBean.getRoom())+""));
                contentList.get(Utils.XXX2XX((bookBean.getRoom()))-1)
                        .setRoomDetail(
                                UIUtils.AddRandomRoomDetail(
                                        2,
                                        bookBean.getStart_time(),
                                        bookBean.getEnd_time(),
                                        contentList.get(Utils.XXX2XX(bookBean.getRoom())-1).getRoomDetail()
                                )
                        );
            }
        }
        bookBeanList.addAll(newBookBeans);

    }

    public int getCurrentDate() {
        return currentDate;
    }

    public void changeData(String newDay){
        contentList.clear();
        List<BookBean> oldBeanList = bookBeanList;
        bookBeanList = WorkDatabase.getInstance(MeetingRoomActivity.this).getOneDayBookingData(newDay,getSharedPreferences("UserInfo",MODE_PRIVATE).getString("user_id","null"));
        initContentDataList();
//        adapter = new
        adapter.notifyDataSetChanged();
        Intent intent = new Intent();
        intent.setAction(BookingListChangedReceiver.BOOKING_LIST_CHANGED);
        sendBroadcast(intent);
    }
}
