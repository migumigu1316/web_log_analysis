package com.phone.bean.ad;

/**
 * @Description: TODO 每天各省份top3热门广告的数据的封装的实体类
 * @ClassName: AdProvinceTop3
 * @Author: xqg
 * @Date: 2018/11/30 22:24
 */
public class AdProvinceTop3 {
    /**
     * 日期（每天）
     */
    private String date;

    /**
     * 省份
     */
    private String province;

    /**
     * 广告编号
     */
    private int ad_id;

    public AdProvinceTop3() {
    }

    public AdProvinceTop3(String date, String province, int ad_id, int click_count) {
        this.date = date;
        this.province = province;
        this.ad_id = ad_id;
        this.click_count = click_count;
    }

    /**
     * 点击次数
     */
    private int click_count;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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
        return "AdProvinceTop3{" +
                "date='" + date + '\'' +
                ", province='" + province + '\'' +
                ", ad_id=" + ad_id +
                ", click_count=" + click_count +
                '}';
    }
}
