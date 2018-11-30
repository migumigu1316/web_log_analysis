package com.phone.dao.session.impl;

import com.phone.bean.session.Top10CategorySession;
import com.phone.dao.session.ITop10CategorySession;
import com.phone.util.DBCPUtil;
import lombok.Data;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;

/**
 * @Description: TODO 存储top10每个品类的点击top10的session的数据访问层接口实现类
 * @ClassName: Top10CategorySessionImpl
 * @Author: xqg
 * @Date: 2018/11/30 20:47
 */
@Data
public class Top10CategorySessionImpl implements ITop10CategorySession {
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void saveBeanToDB(Top10CategorySession bean) {
        String sql = "insert into top10_category_session values(?,?,?,?)";
        try {
            qr.update(sql, bean.getTask_id(), bean.getCategory_id(), bean.getSession_id(), bean.getClick_count());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
