package com.phone.bean.session;

import lombok.Data;

/**
 * @Description: TODO 按时间比例随机抽取功能抽取出来的session实体类
 * @ClassName: SessionRandomExtract
 * @Author: xqg
 * @Date: 2018/11/29 11:59
 */
@Data
public class SessionRandomExtract {
    /**
     * 任务编号
     */
    private int task_id;
    /**
     * session id
     */
    private String session_id;
    /**
     * session开始时间
     */
    private String start_time;

    /**
     * session结束时间
     */
    private String end_time;

    /**
     * 检索的所有关键字
     */
    private String search_keywords;

    public SessionRandomExtract() {
    }

    public SessionRandomExtract(int task_id, String session_id, String start_time, String end_time, String search_keywords) {
        this.task_id = task_id;
        this.session_id = session_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.search_keywords = search_keywords;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getSearch_keywords() {
        return search_keywords;
    }

    public void setSearch_keywords(String search_keywords) {
        this.search_keywords = search_keywords;
    }
}
