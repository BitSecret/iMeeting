package com.arcsoft.arcfacedemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.connection.ConnectionService;
import com.arcsoft.arcfacedemo.info.BookingInfo;
import com.arcsoft.arcfacedemo.info.UserInfo;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MettingActivity extends Activity {
    TextView bookingIdText[] = new TextView[7];
    TextView RoomIdText[] = new TextView[7];
    TextView BookingDateText[] = new TextView[7];
    TextView UsernameText[] = new TextView[7];
    TextView MeetingNameText[] = new TextView[7];
    TextView ParticipateText[] = new TextView[7];
    TextView StartTimeText[] = new TextView[7];
    TextView EndTimeText[] = new TextView[7];
    TextView StateText[] = new TextView[7];
    TextView TimeText[] = new TextView[7];
    TextView RoomNameTextView;
    TextView meetingNameTextView;
    TextView bookingUserNameTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;
    TextView participateTextView;
    TextView arrivalPersonTextView;
    TextView noticeTextView;
    TextView countDownTextView;
    Handler showMessageHandler;

    public String userId = "";    //基本信息
    public String userName = "";
    public String roomId = "";
    public String roomName = "";

    public String meetingId = "";    //选中的会议的信息
    public String meetingName = "";
    public String meetingStartTime = "";
    public String meetingEndTime = "";
    long timeEndLong;
    long countDown;
    long CountDownHour = 0;
    long CountDownSecond = 0;
    long CountDownMinute = 0;
    public String meetingNote = "暂无提示信息。";
    int meetingParticipate = 1;
    int meetingParticipateArrival = 1;

    BookingInfo bookingInfo;    //会议信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metting);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");    //基本信息
        userName = intent.getStringExtra("userName");
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");
        meetingName = intent.getStringExtra("meetingName");    //会议信息
        meetingId = intent.getStringExtra("meetingId");
        meetingStartTime = intent.getStringExtra("meetingStartTime");
        meetingEndTime = intent.getStringExtra("meetingEndTime");
        meetingNote = intent.getStringExtra("meetingNote");
        timeEndLong = intent.getLongExtra("timeEndLong", 0);

        getMyView();
        getInfo();
    }

    public void onEndingButtonClicked(View view) {
        Intent verifyIntent = new Intent(MettingActivity.this, VerifyActivity.class);
        verifyIntent.putExtra("userId", userId);
        startActivityForResult(verifyIntent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Bundle data = intent.getExtras();
        if (data.getBoolean("result")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("您确定要结束会议吗？");
            dialog.setIcon(R.drawable.ic_launcher_background);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MettingActivity.this, MainActivity.class);
                    intent.putExtra("roomName", roomName);
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                    MettingActivity.this.finish();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });
            dialog.create();
            dialog.show();
        }
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("人脸识别验证失败，不能进行操作！");
            dialog.setIcon(R.drawable.ic_launcher_background);
            dialog.create();
            dialog.show();
        }
    }

    private void getInfo(){
        Thread thread = new Thread(new Runnable() {    //开启线程防止阻塞
            public void run() {
                bookingInfo = new ConnectionService().getBookingInfo(roomId, 0);
                showMessageHandler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }

    private void showMessage(){
        RoomNameTextView.setText("会议室" + roomName);
        meetingNameTextView.setText(meetingName);
        bookingUserNameTextView.setText(userName);
        startTimeTextView.setText(meetingStartTime);
        endTimeTextView.setText(meetingEndTime);
        participateTextView.setText(meetingParticipate + "");
        arrivalPersonTextView.setText(meetingParticipateArrival + "");
        noticeTextView.setText(meetingNote);

        Timestamp ts = new Timestamp(System.currentTimeMillis());    //用于判断会议状态
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        while(bookingInfo != null){
            bookingIdText[count].setText(bookingInfo.bookingId);    //订单号
            RoomIdText[count].setText(bookingInfo.roomNumber);    //房间号
            BookingDateText[count].setText(format.format(bookingInfo.foundingTime));    //预定日期
            UsernameText[count].setText(bookingInfo.userName);   //预定用户名
            MeetingNameText[count].setText(bookingInfo.meetingName);    //会议名
            ParticipateText[count].setText(bookingInfo.content + "");    //容量
            TimeText[count].setText(bookingInfo.bookingDate.toString());    //开会日期
            StartTimeText[count].setText(bookingInfo.startTime.toString());    //开始时间
            EndTimeText[count].setText(bookingInfo.endTime.toString());    //结束时间
            if(ts.before(bookingInfo.endTimestamp)){
                if(((bookingInfo.startTimestamp.getTime() - ts.getTime()) / 1000) > 1800){    //开会时间大于30分钟
                    StateText[count].setText("等待中");     //会议状态
                }
                else{
                    StateText[count].setText("会议中");     //会议状态
                }
            }
            else{
                StateText[count].setText("已结束");     //会议状态
            }
            bookingInfo = bookingInfo.next;
            count++;
            if(count >= 7)
            {
                break;
            }
        }
        //倒计时模块
        countDown = (timeEndLong - System.currentTimeMillis()) / 1000;
        System.out.println(countDown);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (countDown > 0){
                    countDown--;
                    CountDownHour = countDown / 3600 % 24;
                    CountDownMinute = countDown / 60 % 60;
                    CountDownSecond = countDown % 60;
                    showMessageHandler.sendEmptyMessage(2);
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void getMyView() {
        RoomNameTextView = findViewById(R.id.meetingRoomId);
        meetingNameTextView = findViewById(R.id.meetingMeetingName);
        bookingUserNameTextView = findViewById(R.id.meetingHostName);
        startTimeTextView = findViewById(R.id.meetingStartTime);
        endTimeTextView = findViewById(R.id.meetingEndTime);
        participateTextView = findViewById(R.id.meetingParticipate);
        arrivalPersonTextView = findViewById(R.id.meetingArrivalPerson);
        noticeTextView = findViewById(R.id.meetingMeetingNote);
        countDownTextView = findViewById(R.id.meetingCountDownTextView);
        showMessageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 2){
                    String countDownStr = "距离会议结束还剩：" + CountDownHour + "时" + CountDownMinute + "分" + CountDownSecond + "秒";
                    countDownTextView.setText(countDownStr);
                }
                else {
                    showMessage();
                }
            }
        };

        bookingIdText[0] = findViewById(R.id.MeetingbookingIdText1);
        bookingIdText[1] = findViewById(R.id.MeetingbookingIdText2);
        bookingIdText[2] = findViewById(R.id.MeetingbookingIdText3);
        bookingIdText[3] = findViewById(R.id.MeetingbookingIdText4);
        bookingIdText[4] = findViewById(R.id.MeetingbookingIdText5);
        bookingIdText[5] = findViewById(R.id.MeetingbookingIdText6);
        bookingIdText[6] = findViewById(R.id.MeetingbookingIdText7);
        RoomIdText[0] = findViewById(R.id.MeetingRoomIdText1);
        RoomIdText[1] = findViewById(R.id.MeetingRoomIdText2);
        RoomIdText[2] = findViewById(R.id.MeetingRoomIdText3);
        RoomIdText[3] = findViewById(R.id.MeetingRoomIdText4);
        RoomIdText[4] = findViewById(R.id.MeetingRoomIdText5);
        RoomIdText[5] = findViewById(R.id.MeetingRoomIdText6);
        RoomIdText[6] = findViewById(R.id.MeetingRoomIdText7);
        BookingDateText[0] = findViewById(R.id.MeetingBookingDateText1);
        BookingDateText[1] = findViewById(R.id.MeetingBookingDateText2);
        BookingDateText[2] = findViewById(R.id.MeetingBookingDateText3);
        BookingDateText[3] = findViewById(R.id.MeetingBookingDateText4);
        BookingDateText[4] = findViewById(R.id.MeetingBookingDateText5);
        BookingDateText[5] = findViewById(R.id.MeetingBookingDateText6);
        BookingDateText[6] = findViewById(R.id.MeetingBookingDateText7);
        UsernameText[0] = findViewById(R.id.MeetingUsernameText1);
        UsernameText[1] = findViewById(R.id.MeetingUsernameText2);
        UsernameText[2] = findViewById(R.id.MeetingUsernameText3);
        UsernameText[3] = findViewById(R.id.MeetingUsernameText4);
        UsernameText[4] = findViewById(R.id.MeetingUsernameText5);
        UsernameText[5] = findViewById(R.id.MeetingUsernameText6);
        UsernameText[6] = findViewById(R.id.MeetingUsernameText7);
        MeetingNameText[0] = findViewById(R.id.MeetingUserIdText1);
        MeetingNameText[1] = findViewById(R.id.MeetingUserIdText2);
        MeetingNameText[2] = findViewById(R.id.MeetingUserIdText3);
        MeetingNameText[3] = findViewById(R.id.MeetingUserIdText4);
        MeetingNameText[4] = findViewById(R.id.MeetingUserIdText5);
        MeetingNameText[5] = findViewById(R.id.MeetingUserIdText6);
        MeetingNameText[6] = findViewById(R.id.MeetingUserIdText7);
        ParticipateText[0] = findViewById(R.id.MeetingParticipateText1);
        ParticipateText[1] = findViewById(R.id.MeetingParticipateText2);
        ParticipateText[2] = findViewById(R.id.MeetingParticipateText3);
        ParticipateText[3] = findViewById(R.id.MeetingParticipateText4);
        ParticipateText[4] = findViewById(R.id.MeetingParticipateText5);
        ParticipateText[5] = findViewById(R.id.MeetingParticipateText6);
        ParticipateText[6] = findViewById(R.id.MeetingParticipateText7);
        StartTimeText[0] = findViewById(R.id.MeetingStartTimeText1);
        StartTimeText[1] = findViewById(R.id.MeetingStartTimeText2);
        StartTimeText[2] = findViewById(R.id.MeetingStartTimeText3);
        StartTimeText[3] = findViewById(R.id.MeetingStartTimeText4);
        StartTimeText[4] = findViewById(R.id.MeetingStartTimeText5);
        StartTimeText[5] = findViewById(R.id.MeetingStartTimeText6);
        StartTimeText[6] = findViewById(R.id.MeetingStartTimeText7);
        EndTimeText[0] = findViewById(R.id.MeetingEndtiemText1);
        EndTimeText[1] = findViewById(R.id.MeetingEndtiemText2);
        EndTimeText[2] = findViewById(R.id.MeetingEndtiemText3);
        EndTimeText[3] = findViewById(R.id.MeetingEndtiemText4);
        EndTimeText[4] = findViewById(R.id.MeetingEndtiemText5);
        EndTimeText[5] = findViewById(R.id.MeetingEndtiemText6);
        EndTimeText[6] = findViewById(R.id.MeetingEndtiemText7);
        StateText[0] = findViewById(R.id.MeetingStateText1);
        StateText[1] = findViewById(R.id.MeetingStateText2);
        StateText[2] = findViewById(R.id.MeetingStateText3);
        StateText[3] = findViewById(R.id.MeetingStateText4);
        StateText[4] = findViewById(R.id.MeetingStateText5);
        StateText[5] = findViewById(R.id.MeetingStateText6);
        StateText[6] = findViewById(R.id.MeetingStateText7);
        TimeText[0] = findViewById(R.id.MeetingDateText1);
        TimeText[1] = findViewById(R.id.MeetingDateText2);
        TimeText[2] = findViewById(R.id.MeetingDateText3);
        TimeText[3] = findViewById(R.id.MeetingDateText4);
        TimeText[4] = findViewById(R.id.MeetingDateText5);
        TimeText[5] = findViewById(R.id.MeetingDateText6);
        TimeText[6] = findViewById(R.id.MeetingDateText7);
    }
}
