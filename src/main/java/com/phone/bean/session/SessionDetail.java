package com.phone.bean.session;

import lombok.Data;

/**
 * @Description: TODO session的明细数据实体类
 * @ClassName: SessionDetail
 * @Author: xqg
 * @Date: 2018/11/29 22:46
 */
@Data
public class SessionDetail {
    private int task_id;
    private int user_id;
    private String session_id;
    private int page_id;
    private String action_time;
    private String search_keyword;
    private int click_category_id;
    private int click_product_id;
    private String order_category_ids;
    private String order_product_ids;
    private String pay_category_ids;
    private String pay_product_ids;

    public SessionDetail() {
    }

    public SessionDetail(int task_id, int user_id, String session_id, int page_id, String action_time, String search_keyword, int click_category_id, int click_product_id, String order_category_ids, String order_product_ids, String pay_category_ids, String pay_product_ids) {
        this.task_id = task_id;
        this.user_id = user_id;
        this.session_id = session_id;
        this.page_id = page_id;
        this.action_time = action_time;
        this.search_keyword = search_keyword;
        this.click_category_id = click_category_id;
        this.click_product_id = click_product_id;
        this.order_category_ids = order_category_ids;
        this.order_product_ids = order_product_ids;
        this.pay_category_ids = pay_category_ids;
        this.pay_product_ids = pay_product_ids;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public String getAction_time() {
        return action_time;
    }

    public void setAction_time(String action_time) {
        this.action_time = action_time;
    }

    public String getSearch_keyword() {
        return search_keyword;
    }

    public void setSearch_keyword(String search_keyword) {
        this.search_keyword = search_keyword;
    }

    public int getClick_category_id() {
        return click_category_id;
    }

    public void setClick_category_id(int click_category_id) {
        this.click_category_id = click_category_id;
    }

    public int getClick_product_id() {
        return click_product_id;
    }

    public void setClick_product_id(int click_product_id) {
        this.click_product_id = click_product_id;
    }

    public String getOrder_category_ids() {
        return order_category_ids;
    }

    public void setOrder_category_ids(String order_category_ids) {
        this.order_category_ids = order_category_ids;
    }

    public String getOrder_product_ids() {
        return order_product_ids;
    }

    public void setOrder_product_ids(String order_product_ids) {
        this.order_product_ids = order_product_ids;
    }

    public String getPay_category_ids() {
        return pay_category_ids;
    }

    public void setPay_category_ids(String pay_category_ids) {
        this.pay_category_ids = pay_category_ids;
    }

    public String getPay_product_ids() {
        return pay_product_ids;
    }

    public void setPay_product_ids(String pay_product_ids) {
        this.pay_product_ids = pay_product_ids;
    }
}
