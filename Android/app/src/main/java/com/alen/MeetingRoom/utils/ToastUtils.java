package com.alen.MeetingRoom.utils;

import android.content.Context;
import android.widget.Toast;

//吐司工具类
public class ToastUtils {

    private static Toast toast;

    public static void send(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context,"Seek:"+msg,  Toast.LENGTH_SHORT);
        } else {
            toast.setText("Seek:"+msg);
        }
        toast.show();
    }
}
