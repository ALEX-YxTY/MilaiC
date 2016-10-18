package com.meishipintu.milai.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/9.
 */
public class Welfare implements Serializable{

    private String id;
    private String title;
    private String logo;
    private String detail_logo;
    private String rice;
    private String act_desc;
    private String shop_desc;
    private String start_time;
    private String end_time;
    private String cityid;
    private int flag;       //  标注跳转位置 1-机器码，2-未使用，3-兑换页, 4-不跳转

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAct_desc() {
        return act_desc;
    }

    public void setAct_desc(String act_desc) {
        this.act_desc = act_desc;
    }

    public String getShop_desc() {
        return shop_desc;
    }

    public void setShop_desc(String shop_desc) {
        this.shop_desc = shop_desc;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getDetail_logo() {
        return detail_logo;
    }

    public void setDetail_logo(String detail_logo) {
        this.detail_logo = detail_logo;
    }

    public String getRice() {
        return rice;
    }

    public void setRice(String rice) {
        this.rice = rice;
    }

    @Override
    public String toString() {
        return "Welfare{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", logo='" + logo + '\'' +
                ", detail_logo='" + detail_logo + '\'' +
                ", rice='" + rice + '\'' +
                ", act_desc='" + act_desc + '\'' +
                ", shop_desc='" + shop_desc + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", cityid='" + cityid + '\'' +
                '}';
    }

}
