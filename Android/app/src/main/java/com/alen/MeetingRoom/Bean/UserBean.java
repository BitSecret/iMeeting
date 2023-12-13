package com.alen.MeetingRoom.Bean;

public class UserBean {
    /**
     * id : 7
     * name : 123
     * password : a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
     * nick_name : 123
     * face_data_file : /usermedia/userfeature/123_feature_file.txt
     * picture : /usermedia/userimg/WechatIMG3644_e2LMkQl.jpeg
     * email : None
     * phone_number : None
     */

    private int id;
    private String name;
    private String password;
    private String nick_name;
    private String face_data_file;
    private String picture;
    private String email;
    private String phone_number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getFace_data_file() {
        return face_data_file;
    }

    public void setFace_data_file(String face_data_file) {
        this.face_data_file = face_data_file;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
