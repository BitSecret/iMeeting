package com.alen.MeetingRoom.ui.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.app.ActivityManager;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.base.BaseSubscriber;
import com.alen.MeetingRoom.http.HttpConfig;
import com.alen.MeetingRoom.http.HttpHolder;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.service.MQTTService;
import com.alen.MeetingRoom.service.MQTTServiceConnection;
import com.alen.MeetingRoom.ui.MainActivity;
import com.alen.MeetingRoom.utils.Permission;
import com.alen.MeetingRoom.utils.Utils;
import com.alen.MeetingRoom.widget.NoScrollViewPager;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.POST;

public class LoginActivity extends BaseActivity  implements /*MQTTService.IGetMessageCallBack,*/ OnCallBackListener{

    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.vp_login_content)
    NoScrollViewPager vp_login_content;
    @BindView(R.id.btn_complete)
    Button btn_complete;

    private List<Fragment> fragments;
    private LoginPwdFragment loginPwdFragment;//登陆密码模块
    private RegisterFragment registerFragment;//注册模块

//    private MQTTServiceConnection serviceConnection;
    private MQTTService mqttService;


    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @OnClick({R.id.btn_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_complete:
                String json;
                switch (btn_complete.getText().toString()){
                    case "登录":
                        String username = et_phone.getText().toString();
                        String password = loginPwdFragment.getPwd();
                        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
                            sendToast("用户名,密码不能为空!");
                            return;
                        }
                        getSharedPreferences("UserInfo",MODE_PRIVATE).edit().putString("name",username).apply();
                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/x-www-form-urlencoded"), "name="+username+"&password="+password);
/*                        HttpHolder.getInstance(this)
                                .service
                                .login(username,password)
                                .subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<ResponseBody>() {
                                    @Override
                                    public void onSubscribe(Subscription s) {
                                        Log.d("wdasd","onSubscribe : "  );

                                    }

                                    @Override
                                    public void onNext(ResponseBody responseBody) {
                                        Log.d("wdasd","onNext" );
                                        try {
                                            switch (responseBody.string()){
                                                case "0":
                                                    Log.d("wdasd","responseBody is : " + responseBody.string());
                                                    final SharedPreferences userInfoSP = getSharedPreferences("UserInfo",MODE_PRIVATE);
                                                    Log.d("wdasd","Login success " );
                                                    Log.d("wdasd","name is : " );
                                                    new Thread(){
                                                        @Override
                                                        public void run() {
                                                            super.run();
                                                            String name = userInfoSP.getString("name","null");
                                                            Log.d("wdasd","name is : " + name);
                                                            if (null != name){
                                                                WorkServices.getInstance(LoginActivity.this).getUserInfo(name);
                                                                WorkServices.getInstance(LoginActivity.this).getUserOders(name);
                                                            }
                                                        }
                                                    }.start();
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                                    break;
                                                case "10000":
                                                    sendToast("用户名密码错误");
                                                    break;
                                                case "10001":
                                                    sendToast("用户存在错误");
                                                    break;
                                                case "10002":
                                                    sendToast("其它错误");
                                                    break;
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable t) {
                                        t.printStackTrace();
                                        Log.d("wdasd","onError" );
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d("wdasd","onComplete" );

                                    }
                                });*/
                        HttpHolder.getInstance(this).request(HttpHolder.getInstance(this).service.login(body), new BaseSubscriber<ResponseBody>() {
                            @Override
                            public void onNext(ResponseBody responseBody) {
                                super.onNext(responseBody);
                                try {
                                    switch (responseBody.string()){
                                        case "0":
                                            final SharedPreferences userInfoSP = getSharedPreferences("UserInfo",MODE_PRIVATE);
                                            new Thread(){
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    String name = userInfoSP.getString("name",null);
                                                    if (null != name){
                                                        WorkServices.getInstance(LoginActivity.this).getUserInfo(name);
//                                                        WorkServices.getInstance(LoginActivity.this).getUserOders(name);
                                                    }
                                                }
                                            }.start();
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            ActivityManager.getInstance().finishActivity(LoginActivity.this);
                                            break;
                                        case "10000":
                                            sendToast("用户名密码错误");
                                            break;
                                        case "10001":
                                            sendToast("用户存在错误");
                                            break;
                                        case "10002":
                                            sendToast("其它错误");
                                            break;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                super.onError(t);
                                sendToast(errorMsg);
                            }
                        });
//                        startActivity(new Intent(mContext, MainActivity.class));
                        break;
                    case "注册":
                        String username2 = et_phone.getText().toString();
                        String password2 = registerFragment.getPwd();
                        if (StringUtils.isEmpty(username2) || StringUtils.isEmpty(password2)){
                            sendToast("用户名,密码不能为空!");
                            //return;
                        }
                        RequestBody body2 = RequestBody.create(okhttp3.MediaType.parse("application/x-www-form-urlencoded"), "name="+username2+"&password="+password2);
                        HttpHolder.getInstance(this).request(HttpHolder.getInstance(this).service.register(body2), new BaseSubscriber<ResponseBody>() {
                            @Override
                            public void onSubscribe(Subscription s) {
                                super.onSubscribe(s);
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                super.onNext(responseBody);
                                try {
                                    switch (responseBody.string()){
                                        case "0":
                                            sendToast("注册成功！");
                                            break;
                                        case "10001":
                                            sendToast("用户名已注册");
                                            break;
                                        case "10002":
                                            sendToast("其它错误");
                                            break;
                                    }

                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                super.onError(t);
                            }

                            @Override
                            public void onComplete() {
                                super.onComplete();
                            }
                        }
                        );
                        break;
                    case "找回密码":

                        break;
                }
//                sendToast(loginPwdFragment.getPwd());
                break;
        }
    }

    @Override
    public void init(Bundle savedInstanceState) {

        loginPwdFragment = new LoginPwdFragment();
        registerFragment = new RegisterFragment();
        fragments = new ArrayList<>();
        fragments.add(loginPwdFragment);
        fragments.add(registerFragment);

        vp_login_content.setOffscreenPageLimit(2);
        vp_login_content.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        //权限申请
        Permission permission = Permission.getInstance(mContext, new Permission.OnPermissionResultListener() {
            @Override
            public void result(String permission, boolean result) {
                Log.e("11111", "result: " + permission + result);
                if (result) {
                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    }
                } else sendToast("你拒绝此权限");
            }
        });
        permission.start(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

//        serviceConnection = new MQTTServiceConnection();
//        serviceConnection.setIGetMessageCallBack(this);

//        Intent intent = new Intent(this, MQTTService.class);

//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

//    @Override
//    public void setMessage(String topic, String message) {
//        if (topic.equals(HttpConfig.LOGIN_RESPONE_TOPIC)){
//
//        }
//    }

    @Override
    public void forget() {
        vp_login_content.setCurrentItem(1);
        registerFragment.forget();
        btn_complete.setText("找回密码");
    }

    @Override
    public void register() {
        vp_login_content.setCurrentItem(1);
        registerFragment.register();
        btn_complete.setText("注册");
    }

    @Override
    public void back() {
        vp_login_content.setCurrentItem(0);
        btn_complete.setText("登录");
    }

    public class LoginBean{
        String name, password;

        public LoginBean(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }
}
