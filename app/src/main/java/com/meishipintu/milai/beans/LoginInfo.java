package com.meishipintu.milai.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/29.
 */
public class LoginInfo implements Serializable{

    private String password;
    private String key;
    private int from;
    private String name;
    private int sex;
    private String url;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "password='" + password + '\'' +
                ", key='" + key + '\'' +
                ", from=" + from +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", url='" + url + '\'' +
                '}';
    }
}
