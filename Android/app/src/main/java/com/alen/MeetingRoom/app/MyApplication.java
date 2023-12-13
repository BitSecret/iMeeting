package com.alen.MeetingRoom.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {


    public static MyApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "id";
            String channelName = "name";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
