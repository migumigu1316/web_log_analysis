package com.phone.dao.goods;

import com.phone.bean.goods.HotGoodsInfo;

import java.util.List;

/**
 * @Description: TODO 热门商品离线统计数据访问层接口
 * @ClassName: IHotGoodsInfoDao
 * @Author: xqg
 * @Date: 2018/11/30 22:29
 */
public interface IHotGoodsInfoDao {
    /**
     * 将参数指定的集合保存到db中
     *
     * @param beans
     */
    void saveBeansToDB(List<HotGoodsInfo> beans);
}
