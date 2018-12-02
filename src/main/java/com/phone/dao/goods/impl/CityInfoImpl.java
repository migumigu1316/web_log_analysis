package com.phone.dao.goods.impl;

import com.phone.bean.goods.CityInfo;
import com.phone.dao.goods.ICityInfo;
import com.phone.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * @Description: TODO 操作城市信息数据访问层接口实现类
 * @ClassName: CityInfoImpl
 * @Author: xqg
 * @Date: 2018/11/30 22:30
 */
public class CityInfoImpl implements ICityInfo {
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public List<CityInfo> findAllInfos() {//查询DB中city_info中的信息
        try {
            //query查询
            return qr.query("select * from city_info", new BeanListHandler<CityInfo>(CityInfo.class));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}