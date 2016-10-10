package com.meishipintu.milai.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/22.
 */
public class Coupon implements Serializable{

    private double value;
    private String endTime;
    private String couponSn;
    private double minPrice;
    private String name;
    private boolean isMi;
    private String couponShow;

    public String getCouponShow() {
        return couponShow;
    }

    public void setCouponShow(String couponShow) {
        this.couponShow = couponShow;
    }

    public boolean isMi() {
        return isMi;
    }

    public void setMi(boolean mi) {
        isMi = mi;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCouponSn() {
        return couponSn;
    }

    public void setCouponSn(String couponSn) {
        this.couponSn = couponSn;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Coupon{" +
                "value=" + value +
                ", endTime='" + endTime + '\'' +
                ", couponSn='" + couponSn + '\'' +
                ", minPrice=" + minPrice +
                ", name='" + name + '\'' +
                ", isMi=" + isMi +
                ", couponShow='" + couponShow + '\'' +
                '}';
    }

}
