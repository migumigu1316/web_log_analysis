package com.phone.dao.ad;

import com.phone.bean.ad.AdStat;

import java.util.List;

/**
 * @Description: TODO 每天各省各城市各广告的点击量操作DAO层接口
 * @ClassName: IAdStatDao
 * @Author: xqg
 * @Date: 2018/12/1 0:02
 */
public interface IAdStatDao {
    /**
     * 批量更新
     */
    void updateBatch(List<AdStat> beans);
}
