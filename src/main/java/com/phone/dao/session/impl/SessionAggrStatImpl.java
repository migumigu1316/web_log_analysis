package com.phone.dao.session.impl;

import com.phone.bean.session.SessionAggrStat;
import com.phone.dao.session.ISessionAggrStat;
import com.phone.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;

/**
 * @Description: TODO session聚合统计数据访问层接口实现类
 * @ClassName: SessionAggrStatImpl
 * @Author: xqg
 * @Date: 2018/11/28 19:23
 */
public class SessionAggrStatImpl implements ISessionAggrStat {
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void saveBeanToDB(SessionAggrStat bean) {
        try {
            qr.update("insert into session_aggr_stat values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    bean.getTask_id(),
                    bean.getSession_count(),
                    bean.getPeriod_1s_3s(),
                    bean.getPeriod_4s_6s(),
                    bean.getPeriod_7s_9s(),
                    bean.getPeriod_10s_30s(),
                    bean.getPeriod_30s_60s(),
                    bean.getPeriod_1m_3m(),
                    bean.getPeriod_3m_10m(),
                    bean.getPeriod_10m_30m(),
                    bean.getPeriod_30m(),
                    bean.getStep_1_3(),
                    bean.getStep_4_6(),
                    bean.getStep_7_9(),
                    bean.getStep_10_30(),
                    bean.getStep_30_60(),
                    bean.getStep_60());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
