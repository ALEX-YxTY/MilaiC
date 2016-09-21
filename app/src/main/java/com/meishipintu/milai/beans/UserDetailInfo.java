package com.meishipintu.milai.beans;

/**
 * Created by Administrator on 2016/8/23.
 */
public class UserDetailInfo {

    private String uid;
    private String name;
    private String realname;
    private String address;
    private int sex;
    private String signature;
    private String tel;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "UserDetailInfo{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", realname='" + realname + '\'' +
                ", address='" + address + '\'' +
                ", sex=" + sex +
                ", signature='" + signature + '\'' +
                ", tel='" + tel + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
