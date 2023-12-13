package com.alen.MeetingRoom.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.app.ActivityManager;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.ui.home.HomeFragment;
import com.alen.MeetingRoom.ui.login.LoginActivity;
import com.alen.MeetingRoom.ui.mine.MineFragment;
import com.alen.MeetingRoom.ui.order.OrderFragment;
import com.alen.MeetingRoom.widget.NoScrollViewPager;
import com.alen.MeetingRoom.widget.TitleBarInfilater;
import com.blankj.utilcode.util.BarUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * SharedPreference: UserInfo
 * name: 123
 * user_id: 12
 * nick_name: 123
 * selected_date: 2019-03-07
 * sessionid: 0g2aie8dbdyfdym968nwhwz4q9dxqs14
 * face_data_file: /usermedia/userfeature/123_feature_file.txt
 * picture: /usermedia/userimg/WechatIMG3644_e2LMkQl.jpeg
 * email: None
 * phone_number: None
 */
public class MainActivity extends BaseActivity implements CallBackListener {

    @BindView(R.id.rg_main)
    RadioGroup rg_main;
    @BindView(R.id.vp_main)
    NoScrollViewPager vp_main;
    private List<Fragment> fragments;
    private HomeFragment homeFragment;
    private OrderFragment orderFragment;
    private MineFragment mineFragment;
    private TitleBarInfilater.Builder builder;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        if (null == getSharedPreferences("UserInfo",MODE_PRIVATE).getString("name",null)){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            ActivityManager.getInstance().finishActivity(this);
        }
//        BarUtils.addMarginTopEqualStatusBarHeight(vp_main);
        builder = TitleBarInfilater.form(mContext, TitleBarInfilater.NULL, TitleBarInfilater.NULL)
                .setElevation(5)
                .setTitleText("首页");
        fragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        fragments.add(homeFragment);
        orderFragment = new OrderFragment();
        fragments.add(orderFragment);
        mineFragment = new MineFragment();
        fragments.add(mineFragment);

        vp_main.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        vp_main.setOffscreenPageLimit(4);
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        builder.setTitleText("首页");
                        vp_main.setCurrentItem(0, true);
                        break;
                    case R.id.rb_order:
                        builder.setTitleText("订单");
                        WorkServices.getInstance(MainActivity.this).getUserOders(getSharedPreferences("UserInfo",MODE_PRIVATE).getString("name","null"));
                        vp_main.setCurrentItem(1, true);
                        break;
                    case R.id.rb_mine:
                        builder.setTitleText("档案");
                        vp_main.setCurrentItem(2, true);
                        break;
                }
            }
        });
        ((RadioButton) rg_main.getChildAt(0)).setChecked(true);
    }

    @Override
    public void gotoOrder() {
        builder.setTitleText("订单");
        WorkServices.getInstance(MainActivity.this).getUserOders(getSharedPreferences("UserInfo",MODE_PRIVATE).getString("name","null"));
        vp_main.setCurrentItem(1, true);
    }
}
