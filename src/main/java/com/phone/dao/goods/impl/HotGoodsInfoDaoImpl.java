package com.phone.dao.goods.impl;

import com.phone.bean.goods.HotGoodsInfo;
import com.phone.dao.goods.IHotGoodsInfoDao;
import com.phone.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;
import java.util.List;

/**
 * @Description: TODO 热门商品离线统计数据访问层接口实现类
 * @ClassName: HotGoodsInfoDaoImpl
 * @Author: xqg
 * @Date: 2018/11/30 22:32
 */
public class HotGoodsInfoDaoImpl implements IHotGoodsInfoDao {
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());


    @Override
    public void saveBeansToDB(List<HotGoodsInfo> beans) {
        String sql = "insert into hot_goods_info values(?,?,?,?,?,?,?,?)";
        Object[][] objs = new Object[beans.size()][];
        for (int i = 0; i < objs.length; i++) {
            HotGoodsInfo bean = beans.get(i);
            objs[i] = new Object[]{bean.getTask_id(), bean.getArea(), bean.getArea_level(), bean.getProduct_id(), bean.getCity_names(), bean.getClick_count(), bean.getProduct_name(), bean.getProduct_status()};
        }

        try {
            qr.batch(sql, objs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
