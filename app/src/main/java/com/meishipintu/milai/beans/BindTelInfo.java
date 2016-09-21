package com.meishipintu.milai.beans;

/**
 * Created by Administrator on 2016/8/30.
 */
public class BindTelInfo {

    private String uid;
    private String mkey;
    private int mfrom;
    private String verify;
    private String memberTel;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return mkey;
    }

    public void setKey(String key) {
        this.mkey = key;
    }

    public int getFrom() {
        return mfrom;
    }

    public void setFrom(int from) {
        this.mfrom = from;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getMemberTel() {
        return memberTel;
    }

    public void setMemberTel(String memberTel) {
        this.memberTel = memberTel;
    }

    @Override
    public String toString() {
        return "BindTelInfo{" +
                "uid='" + uid + '\'' +
                ", mkey='" + mkey + '\'' +
                ", mfrom=" + mfrom +
                ", verify='" + verify + '\'' +
                ", memberTel='" + memberTel + '\'' +
                '}';
    }
}
