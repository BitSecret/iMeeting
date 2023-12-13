package com.alen.MeetingRoom.base;


import android.content.Context;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public abstract class BaseSubscriber<T> implements Subscriber<T> {

    private Context context;

    protected String errorMsg;
    protected static final String SUCCESS_CODE = "00";

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable t) {
        if (t instanceof SocketTimeoutException) {
            errorMsg = "网络请求超时";
        } else if (t instanceof ConnectException) {
            errorMsg = "网络连接异常";
        } else {
            errorMsg = "网络错误";
        }
    }

    @Override
    public void onComplete() {

    }
}
