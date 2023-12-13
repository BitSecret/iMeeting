package com.alen.MeetingRoom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {

    Context context;
    private static final String RITAG = "RITAG";
    SharedPreferences.Editor editor;

    public ReceivedCookiesInterceptor(Context context){
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        editor = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).edit();
        if (!originalResponse.headers("Set-Cookie").isEmpty()){
            String Set_Cookie = originalResponse.headers("Set-Cookie").get(0);
            String[] headers = Set_Cookie.split(";");
            editor.putString("sessionid",headers[0]).apply();
        }

        return originalResponse;
    }
}
