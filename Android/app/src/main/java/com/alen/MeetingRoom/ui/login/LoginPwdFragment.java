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
public class LoginPwdFragment extends BaseFragment {

    @BindView(R.id.et_pwd)
    EditText et_pwd;

    private OnCallBackListener listener;


    public LoginPwdFragment() {
        // Required empty public constructor
    }

    @Override
    protected IPresenter getPresenter() {
        return null;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_login_pwd;
    }

    @OnClick({R.id.tv_forget_pwd, R.id.tv_register})
    public void onViewClicked(View view) {
        if (listener != null){
            switch (view.getId()) {
                case R.id.tv_forget_pwd:
                    listener.forget();
                    break;
                case R.id.tv_register:
                    listener.register();
                    break;
            }
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        listener = (OnCallBackListener) getActivity();
    }

    public String getPwd(){
        return et_pwd.getText().toString().trim();
    }

}
