package com.meishipintu.milai.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/4.
 */
public class UserInfo implements Serializable{
    private String uid;
    private String name;
    private String url;
    private int sex;
    private String tel;
    private String number;
    private int from;
    private int grab_left;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getGrab_left() {
        return grab_left;
    }

    public void setGrab_left(int grab_left) {
        this.grab_left = grab_left;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", sex=" + sex +
                ", tel=" + tel +
                ", number=" + number +
                ", from=" + from +
                ", grab_left=" + grab_left +
                '}';
    }
}
