package com.meishipintu.milai.beans;

/**
 * Created by Administrator on 2016/8/25.
 */
public class GetVCodeRequest {
    String memberTel;

    public GetVCodeRequest(String memberTel) {
        this.memberTel = memberTel;
    }

    public String getMemberTel() {
        return memberTel;
    }

    public void setMemberTel(String memberTel) {
        this.memberTel = memberTel;
    }
}
