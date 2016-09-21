package com.meishipintu.milai.beans;

/**
 * Created by Administrator on 2016/8/25.
 */
public class ConsumeRecordInfo {

    private String shopName;
    private float tradeValue;
    private float couponDiscount;
    private float miDiscount;
    private String createTime;
    private float realPay;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public float getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(float tradeValue) {
        this.tradeValue = tradeValue;
    }

    public float getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(float couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public float getMiDiscount() {
        return miDiscount;
    }

    public void setMiDiscount(float miDiscount) {
        this.miDiscount = miDiscount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public float getRealPay() {
        return realPay;
    }

    public void setRealPay(float realPay) {
        this.realPay = realPay;
    }
}
