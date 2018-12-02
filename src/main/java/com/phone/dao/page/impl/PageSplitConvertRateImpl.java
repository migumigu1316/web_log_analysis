package com.phone.dao.page.impl;

import com.phone.bean.page.PageSplitConvertRate;
import com.phone.dao.page.IPageSplitConvertRate;
import com.phone.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;

/**
 * @Description: TODO 页面单跳转化率数据访问层接口实现类
 * @ClassName: PageSplitConvertRateImpl
 * @Author: xqg
 * @Date: 2018/11/30 22:28
 */
public class PageSplitConvertRateImpl implements IPageSplitConvertRate {
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public void saveBeanToDB(PageSplitConvertRate bean) {
        String sql = "insert into page_split_convert_rate values(?,?)";

        try {
            qr.update(sql, bean.getTask_id(), bean.getConvert_rate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
