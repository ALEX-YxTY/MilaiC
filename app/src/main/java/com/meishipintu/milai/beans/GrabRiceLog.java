package com.meishipintu.milai.beans;

/**
 * Created by Administrator on 2016/8/21.
 */
public class GrabRiceLog {
    private String id;
    private String mobile;
    private String rice;
    private String time;
    private String rid;
    private RiceInfo rinfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRice() {
        return rice;
    }

    public void setRice(String rice) {
        this.rice = rice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public RiceInfo getRinfo() {
        return rinfo;
    }

    public void setRinfo(RiceInfo rinfo) {
        this.rinfo = rinfo;
    }

    @Override
    public String toString() {
        return "GrabRiceLog{" +
                "id='" + id + '\'' +
                ", mobile='" + mobile + '\'' +
                ", rice='" + rice + '\'' +
                ", time='" + time + '\'' +
                ", rid='" + rid + '\'' +
                ", rinfo=" + rinfo.toString() +
                '}';
    }
}
