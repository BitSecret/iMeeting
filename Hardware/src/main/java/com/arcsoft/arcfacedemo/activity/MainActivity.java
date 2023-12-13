package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.common.Constants;
import com.arcsoft.arcfacedemo.connection.ConnectionService;
import com.arcsoft.arcfacedemo.info.BookingInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**主界面，程序默认的显示页面，展示会议室信息*/
public class MainActivity extends Activity {
    //xml布局的部件
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
    TextView loginTextView;
    TextView mainRoomNameTextView;
    Button LoginButton;
    Handler showMessageHandler;


    //本窗口变量
    public boolean isLogin = false;    //先改成true，用于调试
    BookingInfo bookingInfo;    //预订信息
    public String userId = "30";
    public String userName = "Khan";
    public String roomId = "194";
    public String roomName = "101";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(intent.getStringExtra("roomId") != null){   //从其他activity返回
            roomId = intent.getStringExtra("roomId");
            roomName = intent.getStringExtra("roomName");
        }
        else{
            activeEngine();    //第一次需要激活引擎
        }

        getMyView();    //得到组件
        getBookingInfo();
    }

    public void OnOpenButtonClicked(View view) {    //开启会议室按钮
        if(isLogin)
        {
            Intent openIntent = new Intent(MainActivity.this, OpenActivity.class);
            openIntent.putExtra("userId", userId);
            openIntent.putExtra("userName", userName);
            openIntent.putExtra("roomId", roomId);
            openIntent.putExtra("roomName", roomName);
            startActivity(openIntent);
            MainActivity.this.finish();
        }
        else
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("您还未登录！");
            dialog.setIcon(R.drawable.ic_launcher_background);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent, 0);
                }
            });
            dialog.create();
            dialog.show();
        }
    }

    public void OnLoginButtonClicked(View view) {     //登录按钮相应函数
        if(!isLogin)    //如果没登录，登录
        {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(loginIntent, 0);
        }
        else    //登录了,注销
        {
            String pleaseLoginText = "请登录";
            loginTextView.setText(pleaseLoginText);
            LoginButton.setText("登录");
            isLogin = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {   //接受LoginActivity返回的结果
        Bundle data = intent.getExtras();
        isLogin = data.getBoolean("result");
        userId = data.getString("userId");
        userName = data.getString("userName");

        if (isLogin)    //如果验证成功
        {
            String pleaseLoginText = "欢迎您，" + userName;
            loginTextView.setText(pleaseLoginText);
            LoginButton.setText("注销");
        }
    }

    private void getBookingInfo(){
        Thread thread = new Thread(new Runnable() {    //开启线程防止阻塞
            public void run() {
                bookingInfo = new ConnectionService().getBookingInfo(roomId, 0);
                showMessageHandler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }
    private void showMessage(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());    //用于判断会议状态
        mainRoomNameTextView.setText("会议室" + roomName);
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
                    StateText[count].setText("待开始");     //会议状态
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
    }
    private void getMyView() {
        loginTextView = findViewById(R.id.mainPleaseLoginTextView);
        LoginButton = findViewById(R.id.mainLoginButton);
        mainRoomNameTextView = findViewById(R.id.mainTitleRoomName);
        showMessageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                showMessage();
            }
        };

        bookingIdText[0] = findViewById(R.id.MainbookingIdText1);
        bookingIdText[1] = findViewById(R.id.MainbookingIdText2);
        bookingIdText[2] = findViewById(R.id.MainbookingIdText3);
        bookingIdText[3] = findViewById(R.id.MainbookingIdText4);
        bookingIdText[4] = findViewById(R.id.MainbookingIdText5);
        bookingIdText[5] = findViewById(R.id.MainbookingIdText6);
        bookingIdText[6] = findViewById(R.id.MainbookingIdText7);
        RoomIdText[0] = findViewById(R.id.MainRoomIdText1);
        RoomIdText[1] = findViewById(R.id.MainRoomIdText2);
        RoomIdText[2] = findViewById(R.id.MainRoomIdText3);
        RoomIdText[3] = findViewById(R.id.MainRoomIdText4);
        RoomIdText[4] = findViewById(R.id.MainRoomIdText5);
        RoomIdText[5] = findViewById(R.id.MainRoomIdText6);
        RoomIdText[6] = findViewById(R.id.MainRoomIdText7);
        BookingDateText[0] = findViewById(R.id.MainBookingDateText1);
        BookingDateText[1] = findViewById(R.id.MainBookingDateText2);
        BookingDateText[2] = findViewById(R.id.MainBookingDateText3);
        BookingDateText[3] = findViewById(R.id.MainBookingDateText4);
        BookingDateText[4] = findViewById(R.id.MainBookingDateText5);
        BookingDateText[5] = findViewById(R.id.MainBookingDateText6);
        BookingDateText[6] = findViewById(R.id.MainBookingDateText7);
        UsernameText[0] = findViewById(R.id.MainUsernameText1);
        UsernameText[1] = findViewById(R.id.MainUsernameText2);
        UsernameText[2] = findViewById(R.id.MainUsernameText3);
        UsernameText[3] = findViewById(R.id.MainUsernameText4);
        UsernameText[4] = findViewById(R.id.MainUsernameText5);
        UsernameText[5] = findViewById(R.id.MainUsernameText6);
        UsernameText[6] = findViewById(R.id.MainUsernameText7);
        MeetingNameText[0] = findViewById(R.id.MainUserIdText1);
        MeetingNameText[1] = findViewById(R.id.MainUserIdText2);
        MeetingNameText[2] = findViewById(R.id.MainUserIdText3);
        MeetingNameText[3] = findViewById(R.id.MainUserIdText4);
        MeetingNameText[4] = findViewById(R.id.MainUserIdText5);
        MeetingNameText[5] = findViewById(R.id.MainUserIdText6);
        MeetingNameText[6] = findViewById(R.id.MainUserIdText7);
        ParticipateText[0] = findViewById(R.id.MainParticipateText1);
        ParticipateText[1] = findViewById(R.id.MainParticipateText2);
        ParticipateText[2] = findViewById(R.id.MainParticipateText3);
        ParticipateText[3] = findViewById(R.id.MainParticipateText4);
        ParticipateText[4] = findViewById(R.id.MainParticipateText5);
        ParticipateText[5] = findViewById(R.id.MainParticipateText6);
        ParticipateText[6] = findViewById(R.id.MainParticipateText7);
        StartTimeText[0] = findViewById(R.id.MainStartTimeText1);
        StartTimeText[1] = findViewById(R.id.MainStartTimeText2);
        StartTimeText[2] = findViewById(R.id.MainStartTimeText3);
        StartTimeText[3] = findViewById(R.id.MainStartTimeText4);
        StartTimeText[4] = findViewById(R.id.MainStartTimeText5);
        StartTimeText[5] = findViewById(R.id.MainStartTimeText6);
        StartTimeText[6] = findViewById(R.id.MainStartTimeText7);
        EndTimeText[0] = findViewById(R.id.MainEndtiemText1);
        EndTimeText[1] = findViewById(R.id.MainEndtiemText2);
        EndTimeText[2] = findViewById(R.id.MainEndtiemText3);
        EndTimeText[3] = findViewById(R.id.MainEndtiemText4);
        EndTimeText[4] = findViewById(R.id.MainEndtiemText5);
        EndTimeText[5] = findViewById(R.id.MainEndtiemText6);
        EndTimeText[6] = findViewById(R.id.MainEndtiemText7);
        StateText[0] = findViewById(R.id.MainStateText1);
        StateText[1] = findViewById(R.id.MainStateText2);
        StateText[2] = findViewById(R.id.MainStateText3);
        StateText[3] = findViewById(R.id.MainStateText4);
        StateText[4] = findViewById(R.id.MainStateText5);
        StateText[5] = findViewById(R.id.MainStateText6);
        StateText[6] = findViewById(R.id.MainStateText7);;
        TimeText[0] = findViewById(R.id.MainMeetingTime1);
        TimeText[1] = findViewById(R.id.MainMeetingTime2);
        TimeText[2] = findViewById(R.id.MainMeetingTime3);
        TimeText[3] = findViewById(R.id.MainMeetingTime4);
        TimeText[4] = findViewById(R.id.MainMeetingTime5);
        TimeText[5] = findViewById(R.id.MainMeetingTime6);
        TimeText[6] = findViewById(R.id.MainMeetingTime7);

    }


    /**激活引擎部分*/
    private Toast toast = null;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    public void activeEngine() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                FaceEngine faceEngine = new FaceEngine();
                int activeCode = faceEngine.active(MainActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            showToast(getString(R.string.active_success));
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast(getString(R.string.already_activated));
                        } else {
                            showToast(getString(R.string.active_failed, activeCode));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                activeEngine();
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    private void showToast(String s) {
        if (toast == null) {
            toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(s);
            toast.show();
        }
    }
}