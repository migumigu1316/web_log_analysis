package com.phone.bean.ad;

/**
 * @Description: TODO 每天各用户对各广告的点击次数封装实体类
 * @ClassName: AdUserClickCount
 * @Author: xqg
 * @Date: 2018/11/30 22:26
 */
public class AdUserClickCount {
    /**
     * 日期 （每天）
     */
    private String date;

    /**
     * 用户编号
     */
    private int user_id;


    /**
     * 广告编号
     */
    private int ad_id;

    /**
     * 点击次数
     */
    private int click_count;

    public AdUserClickCount() {
    }

    public AdUserClickCount(String date, int user_id, int ad_id, int click_count) {
        this.date = date;
        this.user_id = user_id;
        this.ad_id = ad_id;
        this.click_count = click_count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAd_id() {
        return ad_id;
    }

    public void setAd_id(int ad_id) {
        this.ad_id = ad_id;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }

    @Override
    public String toString() {
        return "AdUserClickCount{" +
                "date='" + date + '\'' +
                ", user_id=" + user_id +
                ", ad_id=" + ad_id +
                ", click_count=" + click_count +
                '}';
    }
}
