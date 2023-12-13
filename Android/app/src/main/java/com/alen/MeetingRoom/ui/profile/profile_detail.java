package com.alen.MeetingRoom.ui.profile;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.broadcast.ProfileChangedReceiver;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.interfaces.ChangeProfileListener;
import com.alen.MeetingRoom.widget.TitleBarInfilater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.http.PartMap;

public class profile_detail extends BaseActivity {


    @BindView(R.id.profile_detail_user_card)
    CardView profile_detail_user_card;
    @BindView(R.id.profile_detail_email_card)
    CardView profile_detail_email_card;
    @BindView(R.id.profile_detail_phone_card)
    CardView profile_detail_phone_card;
    @BindView(R.id.profile_detail_nickname)
    TextView profile_detail_nickname;
    @BindView(R.id.profile_detail_username)
    TextView profile_detail_username;
    @BindView(R.id.profile_detail_email_text)
    TextView profile_detail_email_text;
    @BindView(R.id.profile_detail_phone_text)
    TextView profile_detail_phone_text;
    @BindView(R.id.profile_detail_avatar)
    ImageView profile_detail_avatar;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Bitmap avatarBitmap;
    private AlertDialog.Builder mBuilder;
    public static final int CHANGE_NICKNAME = 1;
    public static final int CHANGE_PASSWORD = 2;
    public static final int CHANGE_AVATAR = 3;
    public static final int CHANGE_EMAIL = 4;
    public static final int CHANGE_PHONE_UMBER = 5;

    private static final int CHOOSE_PHOTO = 1000;
    private static final int CROP_PHOTO = 1001;

    private ProfileChangedReceiver receiver;
    private Uri imageUri;
    private String filePath = null;

    @Override
    public int getLayout() {
        return R.layout.activity_profile_detial;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        TitleBarInfilater.form(mContext).setTitleText("详情").setElevation(5);
        initDatas();
        initViews();
        initBroadCast();
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ProfileChangedReceiver.CHANGE_PROFILE);
        receiver = new ProfileChangedReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int msg = intent.getIntExtra("type",-1);
                switch (msg){
                    case profile_detail.CHANGE_EMAIL:
                        profile_detail_email_text.setText(String.format("邮箱：%s",sharedPreferences.getString("email","Null")));
                        break;
                    case profile_detail.CHANGE_PHONE_UMBER:
                        profile_detail_phone_text.setText(String.format("电话：%s",sharedPreferences.getString("phone_number","Null")));
                        break;
                }
            }
        };
        registerReceiver(receiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initDatas() {
        sharedPreferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
        editor = getSharedPreferences("UserInfo",MODE_PRIVATE).edit();
    }

    private void initViews() {
        String fileName = sharedPreferences.getString("name",null)+".jpg";
        if (!fileName.isEmpty()){
            avatarBitmap = BitmapFactory.decodeFile(this.getCacheDir().getAbsoluteFile()+"/"+fileName);
            profile_detail_avatar.setImageBitmap(avatarBitmap);
        }
        if (null == avatarBitmap){
            profile_detail_avatar.setImageResource(R.drawable.icon_user_default);
        }
        profile_detail_nickname.setText(String.format("昵称：%s", sharedPreferences.getString("nick_name", "Null")));
        profile_detail_username.setText(String.format("用户名：%s",sharedPreferences.getString("name","Null")));
        profile_detail_email_text.setText(String.format("邮箱：%s",sharedPreferences.getString("email","Null")));
        profile_detail_phone_text.setText(String.format("电话：%s",sharedPreferences.getString("phone_number","Null")));
    }

    @OnClick(R.id.profile_detail_email_card)
    public void changeEmail(){
        initDialog(profile_detail.CHANGE_EMAIL).create().show();
    }
    @OnClick(R.id.profile_detail_phone_card)
    public void changePhone(){
        initDialog(profile_detail.CHANGE_PHONE_UMBER).create().show();
    }
    @OnClick(R.id.profile_detail_avatar)
    public void changeAvatar(){
//        File file = new File(this.getCacheDir().getAbsoluteFile()+"/" + getSharedPreferences("UserInfo",MODE_PRIVATE).getString("name","null") + ".jpg");
////        File file = new File(getExternalCacheDir()+ getSharedPreferences("UserInfo",MODE_PRIVATE).getString("name","null") + ".jpg");
//        Log.d("pathlog",getExternalCacheDir()+"");
//        if (file.exists()){
//            file.delete();
//        }
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // open album
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
//        if (Build.VERSION.SDK_INT < 24) {
//            imageUri = Uri.fromFile(file);
//        }else {
//            imageUri = FileProvider.getUriForFile(profile_detail.this,"com.alen.MeetingRoom.fileprovider",file);
//        }
        intent.putExtra("crop",true);
        intent.putExtra("scale",true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,CHOOSE_PHOTO);
//        WorkServices.getInstance(this).updateUserInfo(profile_detail.CHANGE_AVATAR,null);
    }
    /*
     * 1；修改 name
     * 2, 修改密码
     * 3，修改头像
     * 4，修改email
     * 5，修改 phone_number
     */
    private AlertDialog.Builder initDialog(final int type){
        mBuilder = new AlertDialog.Builder(this);
        final String[] value = new String[1];
        View inflaterView = LayoutInflater.from(this).inflate(R.layout.dialog_profile,null);
        final EditText editText = inflaterView.findViewById(R.id.profile_dialog_ed);
        switch (type){
            case profile_detail.CHANGE_NICKNAME:
                break;
            case profile_detail.CHANGE_PASSWORD:
                break;
            case profile_detail.CHANGE_AVATAR:
                break;
            case profile_detail.CHANGE_EMAIL:
                mBuilder.setTitle("修改邮箱");
                editText.setHint("请输入新的邮箱地址");
                break;
            case profile_detail.CHANGE_PHONE_UMBER:
                mBuilder.setTitle("修改电话");
                editText.setHint("请输入新的电话");
                break;
        }

        mBuilder.setView(inflaterView);
        mBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                value[0] = editText.getText().toString();
                WorkServices.getInstance(profile_detail.this).updateUserInfo(type, value[0]);
            }
        });
        mBuilder.setNegativeButton("取消",null);
        return mBuilder;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT > 19){
                        bitmap = handleImageOnKitKat(data);
                    }else {
                        bitmap = handleImageBelowKitKat(data);
                    }
                }
        }
        if (null!=bitmap && null != filePath ){
            File file = new File(filePath);
            WorkServices.getInstance(this).updateUserInfo(3,filePath);
            profile_detail_avatar.setImageBitmap(bitmap);
        }
    }

    private Bitmap handleImageBelowKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(imagePath);
        if (null!=bitmap)
            return bitmap;
        else
            return null;
    }

    private Bitmap handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
//        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理`
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (null != bitmap) {
            return bitmap;
        }
        return null;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = this.getContentResolver().query(uri,null,selection,null,null);
        if (null != cursor){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }
        cursor.close();
        filePath = path;
//        Log.d("pathlog", "path :" + path);
        return path;
    }
}
