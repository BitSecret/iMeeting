package com.alen.MeetingRoom.http;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.alen.MeetingRoom.Bean.BookBean;
import com.alen.MeetingRoom.Bean.UserBean;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.broadcast.LoadBookingDownReceiver;
import com.alen.MeetingRoom.broadcast.ProfileChangedReceiver;
import com.alen.MeetingRoom.interfaces.AddBookingListener;
import com.alen.MeetingRoom.interfaces.ChangeProfileListener;
import com.alen.MeetingRoom.interfaces.DeleteBookingListener;
import com.alen.MeetingRoom.ui.login.LoginActivity;
import com.alen.MeetingRoom.ui.profile.profile_detail;
import com.alen.MeetingRoom.utils.Utils;
import com.alen.MeetingRoom.utils.WorkDatabase;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class WorkServices {

    private static BaseActivity context;
    private static WorkServices services;
    private WorkDatabase database;
    private ChangeProfileListener changeProfileListener;
    private String imageUrl;
    private DeleteBookingListener deleteBookingListener;
    private AddBookingListener addBookingListener;

    public void setAddBookingListener(AddBookingListener addBookingListener) {
        this.addBookingListener = addBookingListener;
    }

    public void setDeleteBookingListener(DeleteBookingListener listener){
        this.deleteBookingListener = listener;
    }

    public void setChangeProfileListener(ChangeProfileListener changeProfileListener){
        this.changeProfileListener = changeProfileListener;
    }

    public WorkServices(BaseActivity context){
        this.context = context;
        if (null == database){
            database = WorkDatabase.getInstance(context);
        }
    }

    public static WorkServices getInstance(BaseActivity context){
        if (null == services){
            services = new WorkServices(context);
        }
        return services;
    }

    public void booking(final String startTime, final String endTime, final String date, final String roomNumber, String username, final String remark){
//        Log.d()
        HttpHolder.getInstance(context).service.booking(startTime,endTime,date,roomNumber,username,remark)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        Log.d("Booking_Tag","onSubscribe");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
//                        Log.d("Booking_Tag","onNext");
                        String mBody = null;
                        try {
                            mBody = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String regex = "(.*id.*: )(\\d+)(.*)";
                        boolean isMatch = Pattern.matches(regex, mBody);
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(mBody);
//                        Log.d("Booking_Tag","body is : " + mBody);
                        if (isMatch){
                            if (matcher.find()){
                                int myId = Integer.parseInt(matcher.group(2));
//                                Log.d("Booking_Tag","2myId is : " + matcher.group(2));
                                WorkDatabase.getInstance(context).addABooking(new BookBean(
                                        myId,
                                        date,
                                        date,
                                        remark,
                                        startTime,
                                        endTime,
                                        roomNumber
//                                        Utils.XXX2XX(roomNumber)
                                ),context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("user_id","-1"));
//                                Log.d("getRoomTAg","workservice add to database roomNumber is :" + roomNumber + "");

                                addBookingListener.addABook(true);
                                Toast.makeText(context,"预定成功",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            addBookingListener.addABook(false);
                            Toast.makeText(context,"预定失败",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Log.d("Booking_Tag","onError : " + e.toString());

                    }

                    @Override
                    public void onComplete() {
//                        Log.d("Booking_Tag","onComplete");

                    }
                });
    }

    public void getUserOders(String username){
        HttpHolder.getInstance(context).service.getOrders(username)
            .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BookBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("workserviceTag","onSubscribe");
                    }

                    @Override
                    public void onNext(List<BookBean> bookBeans) {
                        if(!database.saveBookingDatas(bookBeans,context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("user_id","-1"))){
                            context.sendToast("加载数据失败");
                            Log.d("workserviceTag","loadFaild");
                        }else {
                            Log.d("workserviceTag","loadsucceed");
                            Intent intent = new Intent();
                            intent.setAction(LoadBookingDownReceiver.ACTION_LOAD_BOOKING_DOWN);
                            getAllBookList();
                            context.sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("workserviceTag","onError : " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("workserviceTag","onComplete");
                    }
                });


    }

    public void getUserInfo(final String username){
        HttpHolder.getInstance(context).service.getUserInfo(username)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        Log.d("qweqweqwe","onSubscribe");
                    }

                    @Override
                    public void onNext(UserBean userBean) {
                        if (null != userBean){
                            SharedPreferences.Editor editor = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).edit();
                            SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
                            editor.putString("user_id",userBean.getId()+"");
                            editor.putString("name",userBean.getName());
                            editor.putString("nick_name",userBean.getNick_name());
                            editor.putString("face_data_file",userBean.getFace_data_file());
                            editor.putString("picture","http://47.106.81.165:8000"+userBean.getPicture());
                            editor.putString("email",userBean.getEmail());
                            editor.putString("phone_number",userBean.getPhone_number());
                            editor.apply();
//                            Log.d("qweqweqwe","start to get pic in user info");
                            getAvatar(sharedPreferences.getString("picture","null"));
                            WorkServices workServices = WorkServices.getInstance(context);
                            workServices.getUserOders(userBean.getName());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        context.sendToast("个人信息获取失败！");
//                        Log.d("qweqweqwe","onError: " + e.toString() + " === " + e.getLocalizedMessage() + " === "  + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
//                        Log.d("qweqweqwe","onComplete");

                    }
                });
    }

    public void deleteBooking(final int book_id){
        HttpHolder.getInstance(context).service.
                deleteBooking(book_id,context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("name","null"))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String rb = responseBody.string();
                            if (rb.contains("删除成功")){
                                if (WorkDatabase.getInstance(context).deleteBooking(book_id+"")){
                                    context.sendToast(rb);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getAllBookList(){
        HttpHolder.getInstance(context).service.getAllBookList()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BookBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<BookBean> bookBeans) {
                        if (null != bookBeans){
                            for (BookBean bookBean : bookBeans){
                                WorkDatabase.getInstance(context).addABooking(new BookBean(
                                        bookBean.getId(),
                                        bookBean.getFound_time(),
                                        bookBean.getBooking_date(),
                                        bookBean.getRemark(),
                                        bookBean.getStart_time(),
                                        bookBean.getEnd_time(),
                                        bookBean.getRoom()
                                ),"-1");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 1；修改 name
     * 2, 修改密码
     * 3，修改头像
     * 4，修改email
     * 5，修改 phone_number
     */
    public void updateUserInfo(final int type, final String value){
        Log.d("pro_tag","value is : " + value + " !!!!");
        Map<String, RequestBody> requestBodyHashMap = new HashMap<>();
        String name = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("name",null);
        // name
        RequestBody body_name = RequestBody.create(MediaType.parse("multipart/form-data"),name);
        requestBodyHashMap.put("name",body_name);
        // value
        RequestBody body_value = null;
        // type
        requestBodyHashMap.put("type",RequestBody.create(MediaType.parse("multipart/form-data"),type+""));
        switch (type) {
            case profile_detail.CHANGE_NICKNAME:
//                body_value = RequestBody.create(MediaType.parse("multipart/form-data"),);
                break;
            case profile_detail.CHANGE_PASSWORD:
//                body_value = RequestBody.create(MediaType.parse("multipart/form-data"),);
                break;
            case profile_detail.CHANGE_AVATAR:
                File file = new File(value);
//                Log.d("pro_tag","new File 's path is : " + file.getAbsolutePath());
                body_value = RequestBody.create(MediaType.parse("image/*"),file);
                if (null != body_value){
                    requestBodyHashMap.put("value"+"\";filename=\""+"qwe.jpg",body_value);
                }
                break;
            case profile_detail.CHANGE_EMAIL:
                body_value = RequestBody.create(MediaType.parse("multipart/form-data"),value);
                requestBodyHashMap.put("value",body_value);
                break;
            case profile_detail.CHANGE_PHONE_UMBER:
                body_value = RequestBody.create(MediaType.parse("multipart/form-data"),value);
                requestBodyHashMap.put("value",body_value);
                break;
        }

        HttpHolder.getInstance(context).service.updateInfo(requestBodyHashMap)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                               @Override
                               public void onSubscribe(Disposable d) {

                               }

                               @Override
                               public void onNext(ResponseBody responseBody) {
                                   String responseBody_ = null;
                                   try {
                                       responseBody_ = responseBody.string();
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }
                                   if (responseBody_.contains("修改成功")) {
                                       context.sendToast("修改成功!");
//                                       Log.d("FileTagTag","Changed succeed");
                                       Intent intent = new Intent();
                                       switch (type) {
                                           case profile_detail.CHANGE_NICKNAME:
                                               break;
                                           case profile_detail.CHANGE_PASSWORD:
                                               break;
                                           case profile_detail.CHANGE_AVATAR:
//                                               changeProfileListener.changeProfileAvatar(bitmap);
                                               String filePath = context.getCacheDir().getAbsolutePath()+"/"+ context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE)
                                                       .getString("name",null)+".jpg";
                                               Bitmap bitmap = BitmapFactory.decodeFile(value);
                                               changeProfileListener.changeProfileAvatar(bitmap);
                                               File file = new File(filePath);
                                               if (file.exists()){
                                                   file.delete();
                                               }
                                               try {
                                                   file.createNewFile();
                                                   BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                                                   bitmap.compress(Bitmap.CompressFormat.JPEG,100,bufferedOutputStream);
                                                   bufferedOutputStream.flush();
                                                   bufferedOutputStream.close();
                                               } catch (FileNotFoundException e) {
//                                                   Log.d("FileTagTag",e.toString());
                                                   e.printStackTrace();
                                               } catch (IOException e) {
//                                                   Log.d("FileTagTag",e.toString());
                                                   e.printStackTrace();
                                               }

                                               break;
                                           case profile_detail.CHANGE_EMAIL:
                                               context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit().putString("email", value).apply();
                                               intent.setAction(ProfileChangedReceiver.CHANGE_PROFILE);
                                               intent.putExtra("type", profile_detail.CHANGE_EMAIL);
                                               break;
                                           case profile_detail.CHANGE_PHONE_UMBER:
                                               context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit().putString("phone_number", value).apply();
                                               intent.setAction(ProfileChangedReceiver.CHANGE_PROFILE);
                                               intent.putExtra("type", profile_detail.CHANGE_PHONE_UMBER);
                                               break;
                                       }
                                       context.sendBroadcast(intent);
                                   } else {
                                       context.sendToast(responseBody_);
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onComplete() {

                               }
                           });
    }
        /*HttpHolder.getInstance(context).service.updateInfo(type,value,context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("name","null"))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String responseBody_ = null;
                        try {
                            responseBody_ = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (responseBody_.contains("修改成功")) {
                            Intent intent = new Intent();
                            switch (type) {
                                case profile_detail.CHANGE_NICKNAME:
                                    break;
                                case profile_detail.CHANGE_PASSWORD:
                                    break;
                                case profile_detail.CHANGE_AVATAR:
                                    break;
                                case profile_detail.CHANGE_EMAIL:
                                    context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit().putString("email", value).apply();
                                    intent.setAction(ProfileChangedReceiver.CHANGE_PROFILE);
                                    intent.putExtra("type", profile_detail.CHANGE_EMAIL);
                                    break;
                                case profile_detail.CHANGE_PHONE_UMBER:
                                    context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit().putString("phone_number", value).apply();
                                    intent.setAction(ProfileChangedReceiver.CHANGE_PROFILE);
                                    intent.putExtra("type", profile_detail.CHANGE_PHONE_UMBER);
                                    break;
                            }
                            context.sendBroadcast(intent);
                        } else {
                            context.sendToast(responseBody_);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        context.sendToast("修改失败");
                    }

                    @Override
                    public void onComplete() {
                    }
                });*/
//    }

    public void getAvatar(String url){
        imageUrl = url;
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null != imageUrl){
                try {
                    URL url = new URL(imageUrl);
                    InputStream inputStream = url.openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    changeProfileListener.changeProfileAvatar(bitmap);

                    String fileName = "/"+context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("name","null")+".jpg";
                    File catchDirFile = new File(context.getCacheDir().getAbsoluteFile()+fileName);

                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(catchDirFile));
                    bitmap.compress(Bitmap.CompressFormat.JPEG,80,bufferedOutputStream);
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}