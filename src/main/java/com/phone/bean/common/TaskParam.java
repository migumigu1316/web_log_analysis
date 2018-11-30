package com.phone.bean.common;

import lombok.Data;

import java.util.List;

/**
 * @Description: TODO TaskParam实体类,对筛选参数进行封装
 * @ClassName: TaskParam
 * @Author: xqg
 * @Date: 2018/11/27 22:00
 */
@Data //自动生成get,set方法
public class TaskParam {
//    {"ages":[0,100],"genders":["男","女"],"professionals":["教师", "工人", "记者", "演员", "厨师", "医生", "护士", "司机", "军人", "律师"],"cities":["南京", "无锡", "徐州", "常州", "苏州", "南通", "连云港", "淮安", "盐城", "扬州"]}
    /**
     * 年龄
     */
    private List<Integer> ages;

    /**
     * 性别
     */
    private List<String> genders;

    /**
     * 职业
     */
    private List<String> professionals;

    /**
     * 城市
     */
    private List<String> cities;

    /**
     * 开始时间
     */
    private String start_time;

    /**
     * 结束时间
     */
    private String end_time;

    /**
     * 页面流
     */
    private List<Integer>  page_flow;

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

    public List<Integer> getPage_flow() {
        return page_flow;
    }

    public void setPage_flow(List<Integer> page_flow) {
        this.page_flow = page_flow;
    }

    public List<Integer> getAges() {
        return ages;
    }

    public void setAges(List<Integer> ages) {
        this.ages = ages;
    }

    public List<String> getGenders() {
        return genders;
    }

    public void setGenders(List<String> genders) {
        this.genders = genders;
    }

    public List<String> getProfessionals() {
        return professionals;
    }

    public void setProfessionals(List<String> professionals) {
        this.professionals = professionals;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public TaskParam() {
    }

    public TaskParam(List<Integer> ages, List<String> genders, List<String> professionals, List<String> cities) {
        this.ages = ages;
        this.genders = genders;
        this.professionals = professionals;
        this.cities = cities;
    }
}
