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
    private boolean isMi;//1是米0不是米
    private String couponShow;
    private String machineCode;
    private String mi_desc;//我的米来米劵解释
    private String title;//
    private String description;//
    private String share_img;//
    private boolean isMachineCode;
    private boolean machineCodeUsed;

    public String getMachineCode() {
        return machineCode;
    }

    public String getMi_desc() {
        return mi_desc;
    }

    public void setMi_desc(String mi_desc) {
        this.mi_desc = mi_desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShare_img() {
        return share_img;
    }

    public void setShare_img(String share_img) {
        this.share_img = share_img;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public boolean isMachineCode() {
        return isMachineCode;
    }

    public void setIsMachineCode(boolean machineCode) {
        isMachineCode = machineCode;
    }

    public boolean isMachineCodeUsed() {
        return machineCodeUsed;
    }

    public void setMachineCodeUsed(boolean machineCodeUsed) {
        this.machineCodeUsed = machineCodeUsed;
    }

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
                ", machineCode='" + machineCode + '\'' +
                ", mi_desc='" + mi_desc + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", share_img='" + share_img + '\'' +
                ", isMachineCode=" + isMachineCode +
                ", machineCodeUsed=" + machineCodeUsed +
                '}';
    }
}
