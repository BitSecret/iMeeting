package com.alen.MeetingRoom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {

    private Context context;

    public AddCookiesInterceptor(Context context){
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString("sessionid","null");
        if (!sessionId.equals("null")){
            builder.addHeader("Cookie",sessionId);
        }
        return chain.proceed(builder.build());
    }

}
