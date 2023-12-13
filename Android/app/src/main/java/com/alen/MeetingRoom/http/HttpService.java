package com.alen.MeetingRoom.http;


import com.alen.MeetingRoom.Bean.BookBean;
import com.alen.MeetingRoom.Bean.UserBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface HttpService {

//    @FormUrlEncoded
    @POST("login")
    Flowable<ResponseBody> login(@Body RequestBody info);
    //@Field("name")String name,@Field("password")String password

    @POST("register")
    Flowable<ResponseBody> register(@Body RequestBody info);

    @POST("booking")
    @FormUrlEncoded
    Observable<ResponseBody> booking(@Field("startTime")String startTime,
                                   @Field("endTime")String endTime,
                                   @Field("date")String date,
                                   @Field("roomNumber")String roomNumber,
                                   @Field("username")String username,
                                   @Field("remark")String remark);

    @GET("http://47.106.81.165:8000/api/orders/{username}")
    Observable<List<BookBean>> getOrders(@Path("username") String username);

    //http://47.106.81.165:8000/api/userinfo/123
//    @Headers("Content-Type:application/json")
    @GET("http://47.106.81.165:8000/api/userinfo/{username}")
    Observable<UserBean> getUserInfo(@Path("username") String username);

    @GET("http://47.106.81.165:8000/{url}")//usermedia/userimg/WechatIMG3644_e2LMkQl.jpeg
    Observable<ResponseBody> getAvater(@Path("url") String url);

    @POST("http://47.106.81.165:8000/api/delete")
    @FormUrlEncoded
    Observable<ResponseBody> deleteBooking(@Field("id") int book_id, @Field("username")String username);

    /*@POST("http://47.106.81.165:8000/api/update")
    @FormUrlEncoded
    Observable<ResponseBody> updateInfo(@Field("type") int type,@Field("value") String value,@Field("name") String name);*/

    @POST("http://47.106.81.165:8000/api/update")
    @Multipart
    Observable<ResponseBody> updateInfo(@PartMap Map<String,RequestBody> requestBodyMap);

    @GET("http://47.106.81.165:8000/api/allbooking")
    Observable<List<BookBean>> getAllBookList();




}
