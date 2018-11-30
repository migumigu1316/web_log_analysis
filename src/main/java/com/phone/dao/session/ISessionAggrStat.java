package com.phone.dao.session;

import com.phone.bean.session.SessionAggrStat;

/**
 * @Description: TODO session聚合统计数据访问层接口
 * @ClassName: ISessionAggrStat
 * @Author: xqg
 * @Date: 2018/11/28 19:22
 */
public interface ISessionAggrStat {
    /**
     * session聚合统计实例保存到DB中
     * @param bean
     */
    void saveBeanToDB(SessionAggrStat bean);
}
