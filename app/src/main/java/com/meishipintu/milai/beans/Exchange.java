package com.meishipintu.milai.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public class Exchange implements Serializable {
    private String status;
    private String msg;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Exchange{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
