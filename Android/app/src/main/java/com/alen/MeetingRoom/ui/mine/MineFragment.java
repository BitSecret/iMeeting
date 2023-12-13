package com.alen.MeetingRoom.ui.mine;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.app.ActivityManager;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.base.BaseFragment;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.interfaces.ChangeProfileListener;
import com.alen.MeetingRoom.ui.login.LoginActivity;
import com.alen.MeetingRoom.ui.profile.profile_detail;
import com.alen.MeetingRoom.utils.WorkDatabase;
import com.alen.MeetingRoom.widget.SettingItemView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends BaseFragment {

    //@BindView(R.id.mine_profile)
//    SettingItemView mine_profile;
    @BindView(R.id.mine_avatar)
    ImageView mine_avatar;
    @BindView(R.id.mine_nickname)
    TextView mine_nickname;
    @BindView(R.id.mine_name)
    TextView mine_name;
    @BindView(R.id.fra_mine_card)
    CardView fra_mine_card;

    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        String fileName = getContext().getCacheDir().getAbsolutePath() +"/"+ getContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("name",null)+".jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        if (null!=bitmap){
            mine_avatar.setImageBitmap(bitmap);
        }
        WorkServices.getInstance((BaseActivity) getContext()).setChangeProfileListener(new ChangeProfileListener() {
            @Override
            public void changeProfileAvatar(final Bitmap bitmap) {
                ((BaseActivity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mine_avatar.setImageBitmap(bitmap);
                    }
                });
            }
        });
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        if (null != sharedPreferences){
            mine_nickname.setText("昵称: "+sharedPreferences.getString("nick_name","null"));
            mine_name.setText("用户名: " + sharedPreferences.getString("name","null123"));
        }

        fra_mine_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),profile_detail.class));
            }
        });

    }

    @OnClick(R.id.fra_btn_logout)
    public void logout(){
        WorkDatabase.getInstance(getContext()).deleteAllBooking();
        getContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE).edit().putString("name",null).apply();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        ActivityManager.getInstance().finishActivity(getActivity());
    }


}
