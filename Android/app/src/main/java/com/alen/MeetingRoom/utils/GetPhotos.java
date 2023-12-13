package com.alen.MeetingRoom.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

public class GetPhotos {

    public static String getRealPathFromUri(Context context, Uri uri){
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19){
            return null;
        }else {
            return null;
        }
    }

    public static String getRealPathFromUriBelowAPI19(Context context, Uri uri){
        return null;
    }

}
