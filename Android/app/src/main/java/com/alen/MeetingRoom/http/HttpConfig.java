package com.alen.MeetingRoom.http;

public class HttpConfig {

    public static final int DEFAULT_TIMEOUT = 10;

    public static final String BASE_URL = "http://47.106.81.165:8000/api/";

//    public static final String MQTT_URL = "tcp://mq.tongxinmao.com:18830";

    public static final String MQTT_URL = "tcp://45.32.43.16:1883";


    public static final String LOGIN_REQUSET_TOPIC = "MeetingRoom/ReqLogin";      //发送登录请求
    public static final String LOGIN_RESPONE_TOPIC = "MeetingRoom/ResLogin";      //接收登录回调
    public static final String REGISTER_REQUSET_TOPIC = "MeetingRoom/ReqRegister";      //发送注册请求
    public static final String REGISTER_RESPONE_TOPIC = "MeetingRoom/ResRegister";      //接收注册回调
}