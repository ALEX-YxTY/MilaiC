package com.meishipintu.milai.beans;

/**
 * Created by Administrator on 2016/8/21.
 */
public class RiceInfo {
    private String title;
    private String img;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "RiceInfo{" +
                "title='" + title + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
