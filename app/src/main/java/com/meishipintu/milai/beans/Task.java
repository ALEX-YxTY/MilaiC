package com.meishipintu.milai.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/9.
 */
public class Task implements Serializable{

    private String id;
    private String title;
    private String start_time;
    private String end_time;
    private String sub_name;
    private String type;//1:正常活动、2：外部活动、3：空
    private String type_detail;//空的时候里面字段为显示字段。其他时候是地址。
    private String logo;
    private String likes;
    private String islikes;
    private String forward;


    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }


    public String getIslikes() {
        return islikes;
    }

    public void setIslikes(String islikes) {
        this.islikes = islikes;
    }


    public String getLisks() {
        return likes;
    }

    public void setLisks(String lisks) {
        this.likes = likes;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
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

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_detail() {
        return type_detail;
    }

    public void setType_detail(String type_detail) {
        this.type_detail = type_detail;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", sub_name='" + sub_name + '\'' +
                ", type='" + type + '\'' +
                ", type_detail='" + type_detail + '\'' +
                ", logo='" + logo + '\'' +
                ", likes='" + likes + '\'' +
                ", islikes='" + islikes + '\'' +
                ", forward='" + forward + '\'' +
                '}';
    }


}
