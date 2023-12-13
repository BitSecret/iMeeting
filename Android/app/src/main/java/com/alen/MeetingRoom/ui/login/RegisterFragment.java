package com.alen.MeetingRoom.ui.login;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.base.BaseFragment;
import com.alen.MeetingRoom.base.IPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseFragment {

    @BindView(R.id.et_pwd)
    EditText et_pwd;

    private OnCallBackListener listener;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    protected IPresenter getPresenter() {
        return null;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_register;
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        if (listener != null){
            switch (view.getId()) {
                case R.id.iv_back:
                    listener.back();
                    break;
            }
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        listener = (OnCallBackListener) getActivity();
    }

    public String getPwd(){return et_pwd.getText().toString().trim();}

    public void clear(){et_pwd.getText().clear();}

    public void register(){et_pwd.setHint("请输入密码"); clear();}

    public void forget(){et_pwd.setHint("请输入新密码"); clear();}
}
