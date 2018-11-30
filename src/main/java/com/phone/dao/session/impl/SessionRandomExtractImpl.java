package com.phone.dao.session.impl;

import com.phone.bean.session.SessionRandomExtract;
import com.phone.dao.session.ISessionRandomExtract;
import com.phone.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;


import java.sql.SQLException;
import java.util.List;

/**
 * @Description: TODO 按时间比例随机抽取功能抽取出来的session 数据访问层接口实现类
 * @ClassName: SessionRandomExtractImpl
 * @Author: xqg
 * @Date: 2018/11/29 14:34
 */
public class SessionRandomExtractImpl implements ISessionRandomExtract {
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void saveBeansToDB(List<SessionRandomExtract> beans) {
        String sql = "insert into session_random_extract values(?,?,?,?,?)";

        //TODO 为什么这样写???
        Object[][] params = new Object[beans.size()][];
        //{{1,"","",""},{}}

        for(int i=0;i<params.length;i++){
            SessionRandomExtract bean = beans.get(i);
            params[i] = new Object[]{bean.getTask_id(),bean.getSession_id(),bean.getStart_time(),bean.getEnd_time(),bean.getSearch_keywords()};
        }

        try {
            qr.batch(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
