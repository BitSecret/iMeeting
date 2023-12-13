package com.alen.MeetingRoom.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permission extends AppCompatActivity {
    private Activity activity;
    private OnPermissionResultListener listener;
    public static Permission getInstance(Activity activity, OnPermissionResultListener listener){
        return new Permission(activity, listener);
    }
    private Permission(Activity activity, OnPermissionResultListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    public void start(String ... permissions){
        List<String> list = Arrays.asList(permissions);
        startList(list);
    }

    public void startList(List<String> list){
        List<String> permissionList = new ArrayList<>();
        for (String permission : list){
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }
    }

    public interface OnPermissionResultListener{
        void result(String permission, boolean result);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("11111", "onRequestPermissionsResult: " + "111111");
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    int i = 0;
                    for (int result : grantResults){
                        if (listener != null) listener.result(permissions[i], result == PackageManager.PERMISSION_GRANTED);
                        Log.e("1111111", "onRequestPermissionsResult: " + permissions[i] + (result == PackageManager.PERMISSION_GRANTED));
                        i++;
                    }
                }
                break;
            default:
        }
    }
}