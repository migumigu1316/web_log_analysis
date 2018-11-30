package com.phone.dao.session.impl;

import com.phone.bean.session.SessionDetail;
import com.phone.dao.session.ISessionDetail;
import com.phone.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;

/**
 * @Description: TODO 操作session的明细数据数据访问层的接口实现类
 * @ClassName: SessionDetailImpl
 * @Author: xqg
 * @Date: 2018/11/29 22:44
 */
public class SessionDetailImpl implements ISessionDetail {
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void saveToDB(SessionDetail bean) {
        String sql = "insert into session_detail values(?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            qr.update(sql, new Object[]{
                    bean.getTask_id(),
                    bean.getUser_id(),
                    bean.getSession_id(),
                    bean.getPage_id(),
                    bean.getAction_time(),
                    bean.getSearch_keyword(),
                    bean.getClick_category_id(),
                    bean.getClick_product_id(),
                    bean.getOrder_category_ids(),
                    bean.getOrder_product_ids(),
                    bean.getPay_category_ids(),
                    bean.getPay_product_ids(),
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
