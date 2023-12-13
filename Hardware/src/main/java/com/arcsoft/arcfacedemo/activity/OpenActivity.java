package com.arcsoft.arcfacedemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.connection.ConnectionService;
import com.arcsoft.arcfacedemo.info.BookingInfo;
import com.arcsoft.arcfacedemo.info.UserInfo;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class OpenActivity extends Activity {
    TextView bookingIdText[] = new TextView[6];
    TextView RoomIdText[] = new TextView[6];
    TextView BookingDateText[] = new TextView[6];
    TextView UsernameText[] = new TextView[6];
    TextView MeetingNameText[] = new TextView[6];
    TextView ParticipateText[] = new TextView[6];
    TextView StartTimeText[] = new TextView[6];
    TextView EndTimeText[] = new TextView[6];
    TextView StateText[] = new TextView[6];
    Button OperatorButton[] = new Button[6];
    TextView TimeText[] = new TextView[6];
    TextView welcomeTextView;
    TextView openTitleRoomNameTextView;
    ImageView userPictureView;
    TextView userNameTextView;
    TextView userIdTextView;
    TextView userPhoneTextView;
    TextView userMailTextView;
    TextView noticeTextView;
    TextView userRankTextView;
    Handler showMessageHandler;

    public String userId = "";    //基本信息
    public String userName = "";
    public String roomId = "";
    public String roomName = "";

    public String meetingId = "";    //选中的会议的信息
    public String meetingName = "";
    public String meetingStartTime = "";
    public String meetingEndTime = "";
    long timeStartLong;
    long timeEndLong;
    public String meetingNote = "暂无提示信息。";

    BookingInfo bookingInfo;    //会议信息
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");
        getMyView();
        getBookingInfo();
    }

    public void onExitButtonClicked(View view)    //退出登录按钮的响应函数
    {
        Intent intent = new Intent(OpenActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        startActivity(intent);
        OpenActivity.this.finish();
    }

    public void onOperationButtonClicked(View view)    //5个操作按钮的响应函数，调用VeryfiActivity(请求码1)验证身份
    {
        int count;
        for(count = 0; count < 6; count++){
            if(OperatorButton[count].getId() == view.getId()){
                break;
            }
        }
        meetingId = bookingIdText[count].getText().toString();
        meetingName = MeetingNameText[count].getText().toString();
        meetingStartTime =  StartTimeText[count].getText().toString();
        meetingEndTime =  EndTimeText[count].getText().toString();
        System.out.println("ad");
        timeStartLong = Timestamp.valueOf(TimeText[count].getText().toString() + " " + StartTimeText[count].getText().toString()).getTime();
        timeEndLong = Timestamp.valueOf(TimeText[count].getText().toString() + " " + EndTimeText[count].getText().toString()).getTime();
        System.out.println("ahahaha");
        //meetingNote =

        Intent verifyIntent = new Intent(OpenActivity.this, VerifyActivity.class);
        verifyIntent.putExtra("userId", userId);
        startActivityForResult(verifyIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)   //接受Activity(响应码0)返回的结果
    {
        System.out.println(userName);
        Bundle data = intent.getExtras();
        if(data.getBoolean("result")) {
            Intent signIntent = new Intent(OpenActivity.this, SignActivity.class);
            signIntent.putExtra("userId", userId);
            signIntent.putExtra("userName", userName);
            signIntent.putExtra("roomName", roomName);
            signIntent.putExtra("roomId", roomId);
            signIntent.putExtra("meetingStartTime", meetingStartTime);
            signIntent.putExtra("meetingEndTime", meetingEndTime);
            signIntent.putExtra("meetingId", meetingId);
            signIntent.putExtra("meetingName", meetingName);
            signIntent.putExtra("meetingNote", meetingNote);
            signIntent.putExtra("timeStartLong", timeStartLong);
            signIntent.putExtra("timeEndLong", timeEndLong);
            startActivity(signIntent);
            OpenActivity.this.finish();
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

    private void showMessage(){
        /**更新预订者信息*/
        welcomeTextView.setText("欢迎您，" + userInfo.userName);
        openTitleRoomNameTextView.setText("会议室" + roomName);
        userNameTextView.setText(userName);
        userIdTextView.setText(userId);
        userPhoneTextView.setText(userInfo.phone);
        userMailTextView.setText(userInfo.email);
        noticeTextView.setText(meetingNote);
        userRankTextView.setText(userInfo.nickName);
        userPictureView.setImageBitmap(userInfo.picture);   //头像

        /**更新预订信息*/
        int count = 0;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp ts = new Timestamp(System.currentTimeMillis());    //用于判断会议状态
        System.out.println(ts.toString());

        while(bookingInfo != null){
            bookingIdText[count].setText(bookingInfo.bookingId);    //订单号
            RoomIdText[count].setText(bookingInfo.roomNumber);    //房间号
            BookingDateText[count].setText(format.format(bookingInfo.foundingTime));    //预定日期
            UsernameText[count].setText(bookingInfo.userName);   //预定用户名
            MeetingNameText[count].setText(bookingInfo.meetingName);    //会议名
            ParticipateText[count].setText(bookingInfo.content + "");    //容量
            TimeText[count].setText(bookingInfo.bookingDate.toString());    //容量
            StartTimeText[count].setText(bookingInfo.startTime.toString());    //开始时间
            EndTimeText[count].setText(bookingInfo.endTime.toString());    //结束时间
            if(ts.before(bookingInfo.endTimestamp)){
                if(((bookingInfo.startTimestamp.getTime() - ts.getTime()) / 1000) > 1800){    //开会时间大于30分钟
                    StateText[count].setText("等待中");     //会议状态
                    OperatorButton[count].setClickable(false);
                    OperatorButton[count].setText("取消");
                }
                else{
                    StateText[count].setText("待开始");     //会议状态
                    if(bookingInfo.roomNumber.equals(roomName)){
                        OperatorButton[count].setClickable(true);
                        OperatorButton[count].setText("开始");
                    }else{
                        OperatorButton[count].setClickable(false);
                        OperatorButton[count].setText("在" + bookingInfo.roomNumber + "开启");
                    }

                }
                System.out.println(((bookingInfo.startTimestamp.getTime() - ts.getTime()) / 1000));
            }
            else{
                StateText[count].setText("已结束");     //会议状态
                OperatorButton[count].setClickable(false);
                OperatorButton[count].setText("删除");
            }
            bookingInfo = bookingInfo.next;
            count++;
            if(count >= 6)
            {
                break;
            }
        }
    }
    private void getBookingInfo(){
        Thread thread = new Thread(new Runnable() {    //开启线程防止阻塞
            public void run() {
                userInfo = new ConnectionService().getUserInfo(userId, 1);
                bookingInfo = new ConnectionService().getBookingInfo(userId, 1);
                showMessageHandler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }
    private void getMyView() {
        bookingIdText[0] = findViewById(R.id.openbookingIdText1);
        bookingIdText[1] = findViewById(R.id.openbookingIdText2);
        bookingIdText[2] = findViewById(R.id.openbookingIdText3);
        bookingIdText[3] = findViewById(R.id.openbookingIdText4);
        bookingIdText[4] = findViewById(R.id.openbookingIdText5);
        bookingIdText[5] = findViewById(R.id.openbookingIdText6);
        RoomIdText[0] = findViewById(R.id.openRoomIdText1);
        RoomIdText[1] = findViewById(R.id.openRoomIdText2);
        RoomIdText[2] = findViewById(R.id.openRoomIdText3);
        RoomIdText[3] = findViewById(R.id.openRoomIdText4);
        RoomIdText[4] = findViewById(R.id.openRoomIdText5);
        RoomIdText[5] = findViewById(R.id.openRoomIdText6);
        BookingDateText[0] = findViewById(R.id.openBookingDateText1);
        BookingDateText[1] = findViewById(R.id.openBookingDateText2);
        BookingDateText[2] = findViewById(R.id.openBookingDateText3);
        BookingDateText[3] = findViewById(R.id.openBookingDateText4);
        BookingDateText[4] = findViewById(R.id.openBookingDateText5);
        BookingDateText[5] = findViewById(R.id.openBookingDateText6);
        UsernameText[0] = findViewById(R.id.openUsernameText1);
        UsernameText[1] = findViewById(R.id.openUsernameText2);
        UsernameText[2] = findViewById(R.id.openUsernameText3);
        UsernameText[3] = findViewById(R.id.openUsernameText4);
        UsernameText[4] = findViewById(R.id.openUsernameText5);
        UsernameText[5] = findViewById(R.id.openUsernameText6);
        MeetingNameText[0] = findViewById(R.id.openUserIdText1);
        MeetingNameText[1] = findViewById(R.id.openUserIdText2);
        MeetingNameText[2] = findViewById(R.id.openUserIdText3);
        MeetingNameText[3] = findViewById(R.id.openUserIdText4);
        MeetingNameText[4] = findViewById(R.id.openUserIdText5);
        MeetingNameText[5] = findViewById(R.id.openUserIdText6);
        ParticipateText[0] = findViewById(R.id.openParticipateText1);
        ParticipateText[1] = findViewById(R.id.openParticipateText2);
        ParticipateText[2] = findViewById(R.id.openParticipateText3);
        ParticipateText[3] = findViewById(R.id.openParticipateText4);
        ParticipateText[4] = findViewById(R.id.openParticipateText5);
        ParticipateText[5] = findViewById(R.id.openParticipateText6);
        StartTimeText[0] = findViewById(R.id.openStartTimeText1);
        StartTimeText[1] = findViewById(R.id.openStartTimeText2);
        StartTimeText[2] = findViewById(R.id.openStartTimeText3);
        StartTimeText[3] = findViewById(R.id.openStartTimeText4);
        StartTimeText[4] = findViewById(R.id.openStartTimeText5);
        StartTimeText[5] = findViewById(R.id.openStartTimeText6);
        EndTimeText[0] = findViewById(R.id.openEndtiemText1);
        EndTimeText[1] = findViewById(R.id.openEndtiemText2);
        EndTimeText[2] = findViewById(R.id.openEndtiemText3);
        EndTimeText[3] = findViewById(R.id.openEndtiemText4);
        EndTimeText[4] = findViewById(R.id.openEndtiemText5);
        EndTimeText[5] = findViewById(R.id.openEndtiemText6);
        StateText[0] = findViewById(R.id.openStateText1);
        StateText[1] = findViewById(R.id.openStateText2);
        StateText[2] = findViewById(R.id.openStateText3);
        StateText[3] = findViewById(R.id.openStateText4);
        StateText[4] = findViewById(R.id.openStateText5);
        StateText[5] = findViewById(R.id.openStateText6);
        OperatorButton[0] = findViewById(R.id.openOperationButton1);
        OperatorButton[1] = findViewById(R.id.openOperationButton2);
        OperatorButton[2] = findViewById(R.id.openOperationButton3);
        OperatorButton[3] = findViewById(R.id.openOperationButton4);
        OperatorButton[4] = findViewById(R.id.openOperationButton5);
        OperatorButton[5] = findViewById(R.id.openOperationButton6);
        welcomeTextView = findViewById(R.id.openPleaseLoginTextView);
        openTitleRoomNameTextView = findViewById(R.id.openTitleRoomName);
        userPictureView = findViewById(R.id.openUserPicture);
        userNameTextView = findViewById(R.id.openUserName);
        userIdTextView = findViewById(R.id.openUserId);
        noticeTextView = findViewById(R.id.openNotice);
        userPhoneTextView = findViewById(R.id.openUserPhone);
        userMailTextView = findViewById(R.id.openUserMail);
        userRankTextView = findViewById(R.id.openUserRank);
        showMessageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                showMessage();
            }
        };
        TimeText[0] = findViewById(R.id.openMeetingDateText1);
        TimeText[1] = findViewById(R.id.openMeetingDateText2);
        TimeText[2] = findViewById(R.id.openMeetingDateText3);
        TimeText[3] = findViewById(R.id.openMeetingDateText4);
        TimeText[4] = findViewById(R.id.openMeetingDateText5);
        TimeText[5] = findViewById(R.id.openMeetingDateText6);
    }
}

