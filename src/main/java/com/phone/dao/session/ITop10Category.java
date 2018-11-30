package com.phone.dao.session;

import com.phone.bean.session.Top10Category;

/**
 * @Description: TODO 操作特定品类点击、下单和支付总数对应的实体类的数据访问层
 * @ClassName: ITop10Category
 * @Author: xqg
 * @Date: 2018/11/30 20:45
 */
public interface ITop10Category {
    /**
     * 将参数指定的实例保存到db中
     *
     * @param bean
     */
    void saveBeanToDB(Top10Category bean);
}
