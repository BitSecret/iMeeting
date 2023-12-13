package com.alen.MeetingRoom.base;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alen.MeetingRoom.app.ActivityManager;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.utils.ToastUtils;
import com.alen.MeetingRoom.utils.UIUtils;


import butterknife.ButterKnife;

public abstract class BaseActivity <T extends IPresenter> extends AppCompatActivity {

    protected T mPresenter;
    protected Activity mContext;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        mContext = this;
        //设置Presenter
        mPresenter = getPresenter();
        //添加activity到任务栈
        ActivityManager.getInstance().addActivity(this);
        //默认竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置透明状态栏, 黑色字体
        UIUtils.setStatusBar(getWindow(), UIUtils.BLACK);
        init(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().finishActivity(this);
    }

    public void sendToast(String msg){
        ToastUtils.send(mContext, msg);
    }

    protected T getPresenter(){
        return null;
    }

    public abstract int getLayout();

    public abstract void init(Bundle savedInstanceState);
}
