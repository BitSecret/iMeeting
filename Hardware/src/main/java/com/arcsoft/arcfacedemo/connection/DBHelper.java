package com.arcsoft.arcfacedemo.connection;

import com.arcsoft.arcfacedemo.common.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBHelper {
    private static Connection con;    //连接

    public DBHelper() {
        this.con = getConnection();
    }

    private static Connection getConnection(){

        String driver_class = "com.mysql.jdbc.Driver";
        String driver_url = "jdbc:mysql://" + Constants.SERVER_IP_AND_PORT + "/" + Constants.DB_NAME  + "?useSSL=true";
        String database_user = Constants.USERNAME;
        String database_password = Constants.PASSWORD;
        try {
            Class.forName(driver_class);
            con=DriverManager.getConnection(driver_url,database_user,database_password);
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public ResultSet getSelectResult(String sql){
        ResultSet resultSet = null;
        try{
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }
}