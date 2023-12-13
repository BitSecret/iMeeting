package com.alen.MeetingRoom.app;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class AppConfig {

    private static RequestOptions circularOptions;

    public static RequestOptions getCircularOptions(){
        if (null == circularOptions){
            circularOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
        }
        return circularOptions;
    }
}
