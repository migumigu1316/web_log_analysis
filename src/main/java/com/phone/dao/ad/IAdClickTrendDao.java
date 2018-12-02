package com.phone.dao.ad;

import com.phone.bean.ad.AdClickTrend;

import java.util.List;

/**
 * @Description: TODO 最近1小时各广告各分钟的点击量处理Dao层接口
 * @ClassName: IAdClickTrendDao
 * @Author: xqg
 * @Date: 2018/12/1 0:04
 */
public interface IAdClickTrendDao {
    /**
     * 批量更新
     */
    void updateBatch(List<AdClickTrend> beans);
}
