package com.phone.bean.page;

/**
 * @Description: TODO 页面单跳转化率实体类
 * @ClassName: PageSplitConvertRate
 * @Author: xqg
 * @Date: 2018/11/30 22:19
 */
public class PageSplitConvertRate {
    /**
     * 任务编号
     */
    private int task_id;

    /**
     * 转化率结果
     */
    private String convert_rate;

    public PageSplitConvertRate() {
    }

    public PageSplitConvertRate(int task_id, String convert_rate) {
        this.task_id = task_id;
        this.convert_rate = convert_rate;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getConvert_rate() {
        return convert_rate;
    }

    public void setConvert_rate(String convert_rate) {
        this.convert_rate = convert_rate;
    }
}

