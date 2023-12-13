package com.arcsoft.arcfacedemo.info;

import android.graphics.Bitmap;
import com.arcsoft.face.FaceFeature;

public class UserInfo{
    public String userId;
    public String userName;
    public String password;
    public String nickName;
    public FaceFeature faceFeature;
    public Bitmap picture;
    public String email;
    public String phone;
    public boolean isArrival = false;
    public UserInfo next;
    public UserInfo last;
}
