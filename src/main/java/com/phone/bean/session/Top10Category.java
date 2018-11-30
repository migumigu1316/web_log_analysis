package com.phone.bean.session;

/**
 * @Description: TODO 封装特定品类点击、下单和支付总数实体类
 * @ClassName: Top10Category
 * @Author: xqg
 * @Date: 2018/11/30 20:43
 */
public class Top10Category {
    private int task_id;
    private int category_id;
    private int click_count;
    private int order_count;
    private int pay_count;


    public Top10Category() {
    }

    public Top10Category(int task_id, int category_id, int click_count, int order_count, int pay_count) {
        this.task_id = task_id;
        this.category_id = category_id;
        this.click_count = click_count;
        this.order_count = order_count;
        this.pay_count = pay_count;
    }


    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public int getPay_count() {
        return pay_count;
    }

    public void setPay_count(int pay_count) {
        this.pay_count = pay_count;
    }

}
