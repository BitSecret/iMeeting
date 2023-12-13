package com.alen.MeetingRoom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;


//SharedPreferences工具类
public class SPUtils {

    private SPUtils spUtils;
    private SharedPreferences sp;

    private static final String DEFAULT_NAME = "default_sp";

    private static final String DEFAULT_STRING = "0x0";

    private static final int DEFAULT_INT = 0x0;

    private static final long DEFAULT_LONG = 0x0;

    private static final float DEFAULT_FLOAT = 0X0;

    private static final boolean DEFAULT_BOOLEAN = false;

    private SPUtils(String fileName, int mode){
        sp = Utils.getContext().getSharedPreferences(fileName, mode);
    }


    //获取实例对象
    public static SPUtils getInstance(String fileName, int mode) {
        return new SPUtils(fileName, mode);
    }

    public static SPUtils getInstance(String fileName) {
        return new SPUtils(fileName, Context.MODE_PRIVATE);
    }

    public static SPUtils getInstance(int mode) {
        return new SPUtils(DEFAULT_NAME, mode);
    }

    public static SPUtils getInstance() {
        return new SPUtils(DEFAULT_NAME, Context.MODE_PRIVATE);
    }


    //写入数据
    public void put(@NonNull String key, String value){
        sp.edit().putString(key,value).apply();
    }

    public void put(@NonNull String key, int value){
        sp.edit().putInt(key,value).apply();
    }

    public void put(@NonNull String key, long value){
        sp.edit().putLong(key,value).apply();
    }

    public void put(@NonNull String key, float value){
        sp.edit().putFloat(key,value).apply();
    }

    public void put(@NonNull String key, boolean value){
        sp.edit().putBoolean(key,value).apply();
    }


    //读取数据
    public String getString(@NonNull String key){
        return getString(key, DEFAULT_STRING);
    }

    public String getString(@NonNull String key, String defaultValue){
        return sp.getString(key, defaultValue);
    }

    public int getInt(@NonNull String key){
        return getInt(key, DEFAULT_INT);
    }

    public int getInt(@NonNull String key, int defaultValue){
        return sp.getInt(key, defaultValue);
    }

    public long getLong(@NonNull String key){
        return getLong(key, DEFAULT_LONG);
    }

    public long getLong(@NonNull String key, long defaultValue){
        return sp.getLong(key, defaultValue);
    }

    public float getFloat(@NonNull String key){
        return getFloat(key, DEFAULT_FLOAT);
    }

    public float getFloat(@NonNull String key, float defaultValue){
        return sp.getFloat(key, defaultValue);
    }

    public boolean getBoolean(@NonNull String key){
        return getBoolean(key, DEFAULT_BOOLEAN);
    }

    public boolean getBoolean(@NonNull String key, boolean defaultValue){
        return sp.getBoolean(key, defaultValue);
    }


    //移除该key
    public void remove(@NonNull String key){
        sp.edit().remove(key);
    }

    //是否存在该key
    public boolean contains(@NonNull String key){
        return sp.contains(key);
    }

    //清除所有数据
    public void clear(){
        sp.edit().clear().apply();
    }
}
