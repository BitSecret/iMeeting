package com.alen.MeetingRoom.base;

import android.app.Activity;


public abstract class BasePresenter<T extends IView> implements IPresenter {

    protected Activity mContext;
    protected T mView;

    public BasePresenter(Activity activity, T view) {
        this.mContext = activity;
        this.mView = view;
    }


}