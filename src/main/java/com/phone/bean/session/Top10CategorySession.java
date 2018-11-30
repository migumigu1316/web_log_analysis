package com.phone.bean.session;

/**
 * @Description: TODO 存储top10每个品类的点击top10的session的实体类
 * @ClassName: Top10CategorySession
 * @Author: xqg
 * @Date: 2018/11/30 20:44
 */
public class Top10CategorySession {
    private int task_id;
    private int category_id;
    private String session_id;
    private int click_count;


    public Top10CategorySession() {
    }

    public Top10CategorySession(int task_id, int category_id, String session_id, int click_count) {
        this.task_id = task_id;
        this.category_id = category_id;
        this.session_id = session_id;
        this.click_count = click_count;
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

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }
}