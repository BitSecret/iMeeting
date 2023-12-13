package com.alen.MeetingRoom.http;

import android.content.Context;

import com.alen.MeetingRoom.BuildConfig;
import com.alen.MeetingRoom.utils.AddCookiesInterceptor;
import com.alen.MeetingRoom.utils.ReceivedCookiesInterceptor;

import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpHolder {

    private static HttpHolder holder;
    public  HttpService service;
    private  Context context;


    private HttpHolder(Context context){
        this.context = context;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG){
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new ReceivedCookiesInterceptor(context))
                .addInterceptor(new AddCookiesInterceptor(context))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(HttpConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(HttpConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        service = retrofit.create(HttpService.class);
    }

    public static HttpHolder getInstance(Context context) {
        if (null == holder){
            synchronized (HttpHolder.class){
                if (null == holder)
                    holder = new HttpHolder(context);
            }
        }
        return holder;
    }


    public void request(Flowable f, Subscriber s) {
        f.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(s);
    }


}
