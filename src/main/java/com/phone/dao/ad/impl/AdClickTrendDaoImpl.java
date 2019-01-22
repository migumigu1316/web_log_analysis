package com.phone.dao.ad.impl;

import com.phone.bean.ad.AdClickTrend;
import com.phone.dao.ad.IAdClickTrendDao;
import com.phone.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description: TODO 最近1小时各广告各分钟的点击量处理Dao层接口实现类
 * @ClassName: AdClickTrendDaoImpl
 * @Author: xqg
 * @Date: 2018/12/2 15:26
 */
public class AdClickTrendDaoImpl implements IAdClickTrendDao {
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void updateBatch(List<AdClickTrend> beans) {
        try {
            //步骤：
            //①准备两个容器分别存储要更新的AdUserClickCount实例和要插入的AdUserClickCount实例
            List<AdClickTrend> updateContainer = new LinkedList<>();
            List<AdClickTrend> insertContainer = new LinkedList<>();

            //②填充容器（一次与db中的记录进行比对，若存在，就添加到更新容器中；否则，添加到保存的容器中）
            String sql = "select click_count from ad_click_trend where `date`=? and ad_id=? and minute=?";
            for (AdClickTrend bean : beans) {
                //ScalarHandler:用于统计表记录的条数
                //BeanHandler:用来将表中每条记录封装到一个实例中
                //BeanListHandler: 用来将表中所有记录封装到一个集合中，集合中每个元素即为：每条记录所封装的实体类对象
                Object click_count = qr.query(sql, new ScalarHandler<>("click_count"), new Object[]{bean.getDate(), bean.getAd_id(), bean.getMinute()});
                if (click_count == null) {
                    insertContainer.add(bean);
                } else {
                    updateContainer.add(bean);
                }
            }

            //③对更新的容器进行批量update操作
            // click_count=click_count+?  <~ ? 证明?传过来的是本batch新增的click_count,不包括过往的历史  (调用处调用：reduceByKey)
            // click_count=?  <~ ? 证明?传过来的是总的click_count （调用出：使用了updateStateByKey）
            sql = "update ad_click_trend set click_count=?  where `date`=? and ad_id=? and minute=?";
            Object[][] params = new Object[updateContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdClickTrend bean = updateContainer.get(i);
                params[i] = new Object[]{bean.getClick_count(), bean.getDate(), bean.getAd_id(), bean.getMinute()};
            }
            qr.batch(sql, params);

            //④对保存的容器进行批量insert操作
            sql = "insert into ad_click_trend values(?,?,?,?)";
            params = new Object[insertContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdClickTrend bean = insertContainer.get(i);
                params[i] = new Object[]{bean.getDate(), bean.getAd_id(), bean.getMinute(), bean.getClick_count()};
            }
            qr.batch(sql, params);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
