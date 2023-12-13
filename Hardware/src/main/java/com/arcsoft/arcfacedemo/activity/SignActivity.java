package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.connection.ConnectionService;
import com.arcsoft.arcfacedemo.info.UserInfo;
import com.arcsoft.arcfacedemo.model.DrawInfo;
import com.arcsoft.arcfacedemo.util.ConfigUtil;
import com.arcsoft.arcfacedemo.util.DrawHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraListener;
import com.arcsoft.arcfacedemo.widget.FaceRectView;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class SignActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener{
    TextView countDownTextView;
    TextView meetingNameTextView;
    TextView hostNameTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;
    TextView participateTextView;
    TextView arrivalPersonTextView;
    TextView meetingNoteTextView;
    TextView roomIdTextView;
    TextView checkMessageTextView;
    Handler showMessageHandler;

    int clickedButtonId;    //记录敲击的按钮。  1是延期，2是取消，3是开始

    public String userId = "";    //基本信息
    public String userName = "";
    public String roomId = "";
    public String roomName = "";

    public String meetingId = "";    //选中的会议的信息
    public String meetingName = "";
    public String meetingStartTime = "";
    public String meetingEndTime = "";
    public String meetingNote = "暂无提示信息。";
    long timeNowLong;
    long timeStartLong;
    long timeEndLong;
    long countDown;
    long CountDownSecond = 0;
    long CountDownMinute = 0;
    int meetingParticipate = 1;
    int meetingParticipateArrival = 0;    //先设置为0，用于测试

    UserInfo hostInfo;    //与会人员信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Intent openIntent = getIntent();
        userId = openIntent.getStringExtra("userId");    //基本信息
        userName = openIntent.getStringExtra("userName");
        roomId = openIntent.getStringExtra("roomId");
        roomName = openIntent.getStringExtra("roomName");
        meetingName = openIntent.getStringExtra("meetingName");    //会议信息
        meetingId = openIntent.getStringExtra("meetingId");
        meetingStartTime = openIntent.getStringExtra("meetingStartTime");
        timeStartLong = openIntent.getLongExtra("timeStartLong", 0);
        timeEndLong = openIntent.getLongExtra("timeEndLong", 0);
        meetingEndTime = openIntent.getStringExtra("meetingEndTime");
        meetingNote = openIntent.getStringExtra("meetingNote");
        getMyView();
        getInfo();

        /**人脸识别模块*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().setAttributes(attributes);
        }
        ConfigUtil.setFtOrient(SignActivity.this, FaceEngine.ASF_OP_90_ONLY);    //设置人脸优先检测方向
        // Activity启动后就锁定为启动时的方向
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            default:
                break;
        }
        //在布局结束后才做初始化操作
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void onButtonClicked(View view) {
        clickedButtonId = view.getId();
        Intent verifyIntent = new Intent(SignActivity.this, VerifyActivity.class);
        verifyIntent.putExtra("userId", userId);
        startActivityForResult(verifyIntent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("收到回复");
        if(requestCode == 0){
            Bundle data = intent.getExtras();
            if(data.getBoolean("result")) {
                if(clickedButtonId == R.id.signPostponeButton) {    //延期
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("提示");
                    dialog.setMessage("您要将会议延迟10分钟吗?");
                    dialog.setIcon(R.drawable.ic_launcher_background);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {   //延期操作未实现
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }    //不做操作
                    });
                    dialog.create();
                    dialog.show();

                }
                else if(clickedButtonId == R.id.signCancelButton)    //取消
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("提示");
                    dialog.setMessage("您确定要取消会议吗？");
                    dialog.setIcon(R.drawable.ic_launcher_background);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignActivity.this, OpenActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("userName", userName);
                            intent.putExtra("roomName", roomName);
                            intent.putExtra("roomId", roomId);
                            startActivity(intent);
                            SignActivity.this.finish();
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }    //不做操作
                    });
                    dialog.create();
                    dialog.show();

                }
                else    //现在开始
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("提示");
                    dialog.setMessage("您确定要现在开始吗？");
                    dialog.setIcon(R.drawable.ic_launcher_background);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SignActivity.this, MettingActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("userName", userName);
                            intent.putExtra("roomName", roomName);
                            intent.putExtra("roomId", roomId);
                            intent.putExtra("meetingStartTime", meetingStartTime);
                            intent.putExtra("meetingEndTime", meetingEndTime);
                            intent.putExtra("meetingParticipate", meetingParticipate);
                            intent.putExtra("meetingId", meetingId);
                            intent.putExtra("meetingName", meetingName);
                            intent.putExtra("meetingNote", meetingNote);
                            intent.putExtra("timeEndLong", timeEndLong);
                            startActivity(intent);
                            SignActivity.this.finish();
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.create();
                    dialog.show();
                }
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
    }

    public void showMessage(){
        meetingNameTextView.setText(meetingName);    //会议的基本信息
        roomIdTextView.setText("会议室" + roomName);
        hostNameTextView.setText(userName);
        //startTimeTextView.setText(meetingStartTime);
        startTimeTextView.setText(meetingStartTime);
        endTimeTextView.setText(meetingEndTime);
        arrivalPersonTextView.setText(meetingParticipateArrival + "");
        meetingNoteTextView.setText(meetingNote);
        UserInfo countInfo = hostInfo;    //计算与会人数
        while(countInfo != null){
            meetingParticipate++;
            countInfo = countInfo.next;
        }
        participateTextView.setText(meetingParticipate + "");

        //倒计时模块
        timeNowLong = System.currentTimeMillis();
        if(timeNowLong > timeStartLong){
            if(timeEndLong - timeStartLong > 10 * 60 * 1000){
                countDown = 10 * 60 * 1000;
            }
            else{
                countDown = timeEndLong - timeStartLong;
            }
        }
        else{
            countDown = timeStartLong - timeNowLong;
        }
        System.out.println(countDown / 1000 / 60 + "分  " + countDown / 1000 % 60 + "秒");
        System.out.println("now:" + timeNowLong);
        System.out.println("start:" + timeStartLong);
        System.out.println("end:" + timeEndLong);
        countDown = countDown / 1000;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (countDown > 0){
                    countDown--;
                    CountDownSecond = countDown % 60;
                    CountDownMinute = countDown / 60;
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
        checkMessageTextView.setText("成功获取与会人员信息，开始签到");
        isDetect = false;
    }

    public void getMyView(){
        previewView = findViewById(R.id.texture_preview);
        faceRectView = findViewById(R.id.face_rect_view);
        countDownTextView = findViewById(R.id.signCountDownTextView);
        meetingNameTextView = findViewById(R.id.signMeetingName);
        hostNameTextView = findViewById(R.id.signHostName);
        startTimeTextView = findViewById(R.id.signStartTime);
        endTimeTextView = findViewById(R.id.signEndTime);
        participateTextView = findViewById(R.id.signParticipate);
        arrivalPersonTextView = findViewById(R.id.signArrivalPerson);
        meetingNoteTextView = findViewById(R.id.signMeetingNote);
        roomIdTextView = findViewById(R.id.signRoomIdTextView);
        checkMessageTextView = findViewById(R.id.signUpNoteText);
        showMessageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0){
                    showMessage();
                }
                else if(msg.what == 2){
                    CountDownSecond = countDown % 60;
                    CountDownMinute = countDown / 60;
                    String countDownStr = CountDownMinute + "分" + CountDownSecond + "秒";
                    countDownTextView.setText(countDownStr);
                }
                else{
                    //checkSign(msg.what);
                    checkMessageTextView.setText("欢迎您，Khan");    //写死
                    arrivalPersonTextView.setText("1");
                }
            }
        };
    }

    private void getInfo(){
        Thread thread = new Thread(new Runnable() {    //开启线程防止阻塞
            public void run() {
                hostInfo = new ConnectionService().getUserInfo(meetingId, 0);
                System.out.println("成功得到host");
                showMessageHandler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }

    private void checkSign(int n){
        if(n == 1){
            if(!currentHost.isArrival){
                currentHost.isArrival = true;
                meetingParticipateArrival++;
                arrivalPersonTextView.setText(meetingParticipateArrival + "");
                checkMessageTextView.setText(currentHost.userName + ",签到成功！");
            }
            else {
                checkMessageTextView.setText(currentHost.userName + "，已经签到，无需再签！");
            }
        }
        else{
            checkMessageTextView.setText(currentHost.userName + "，验证失败，请核对会议信息！");
        }
    }

    /**人脸识别模块*/
    FaceFeature cameraFeature = null;    //两个人脸数据
    FaceFeature databaseFeature = null;
    FaceSimilar similar = new FaceSimilar();
    boolean isDetect = true;    //控制线程开启
    UserInfo currentHost = null;

    private static final String TAG = "SignActivity";
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private FaceEngine faceEngine;
    private FaceEngine comFaceEngine;
    private int afCode = -1;
    private int processMask = FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS;
    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private View previewView;
    private FaceRectView faceRectView;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    private void initEngine() {
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(this.getApplicationContext(), FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                16, 20, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS);

        comFaceEngine = new FaceEngine();
        comFaceEngine.init(this.getApplicationContext(), FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                16, 20, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS | FaceEngine.ASF_FACE_RECOGNITION);

        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);
        if (afCode != ErrorInfo.MOK) {
            Toast.makeText(this, getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show();
        }
    }

    private void unInitEngine() {

        if (afCode == 0) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }
    @Override
    protected void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
        super.onDestroy();
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this.getApplicationContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Log.i(TAG, "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror);
            }


            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                final List<FaceInfo> faceInfoList = new ArrayList<>();
                int code = faceEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    code = faceEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
                    if (code != ErrorInfo.MOK) {
                        return;
                    }
                }else {
                    return;
                }

                List<AgeInfo> ageInfoList = new ArrayList<>();
                List<GenderInfo> genderInfoList = new ArrayList<>();
                List<Face3DAngle> face3DAngleList = new ArrayList<>();
                List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();
                int ageCode = faceEngine.getAge(ageInfoList);
                int genderCode = faceEngine.getGender(genderInfoList);
                int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
                int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);



                 System.out.println("即将进入比对模块");
                if(!isDetect){    //人脸比对签到模块，开启线程防阻塞
                    isDetect = true;

                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            /*UserInfo userInfo = hostInfo;
                            int count = 0;
                            while(userInfo != null){
                                databaseFeature = userInfo.faceFeature;
                                cameraFeature = new FaceFeature();
                                comFaceEngine.extractFaceFeature(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList.get(0), cameraFeature);
                                comFaceEngine.compareFaceFeature(cameraFeature, databaseFeature, similar);
                                if(similar.getScore() > 0.6){
                                    showMessageHandler.sendEmptyMessage(1);
                                    break;
                                }
                                else{
                                    count++;
                                }
                                userInfo = userInfo.next;
                            }
                            if(count == meetingParticipate){
                                showMessageHandler.sendEmptyMessage(-1);
                            }
                            System.out.println("count：" + count);*/
                            showMessageHandler.sendEmptyMessage(-1);    //测试用
                            isDetect = false;
                        }
                    });
                    thread.start();
                }




                //有其中一个的错误码不为0，return
                if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
                    return;
                }
                if (faceRectView != null && drawHelper != null) {
                    List<DrawInfo> drawInfoList = new ArrayList<>();
                    for (int i = 0; i < faceInfoList.size(); i++) {
                        drawInfoList.add(new DrawInfo(faceInfoList.get(i).getRect(), genderInfoList.get(i).getGender(), ageInfoList.get(i).getAge(), faceLivenessInfoList.get(i).getLiveness(), null));
                    }
                    drawHelper.draw(faceRectView, drawInfoList);
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };

        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(),previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
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
                initEngine();
                initCamera();
                if (cameraHelper != null) {
                    cameraHelper.start();
                }
            } else {
                Toast.makeText(this.getApplicationContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //在{@link #previewView}第一次布局完成后，去除该监听，并且进行引擎和相机的初始化
    @Override
    public void onGlobalLayout() {
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
        }
    }
}
