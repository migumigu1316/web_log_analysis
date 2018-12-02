package com.phone.dao.ad;

import com.phone.bean.ad.AdUserClickCount;

import java.util.List;

/**
 * @Description: TODO 统计每天各用户对各广告的点击次数功能接口
 * @ClassName: IAdUserClickCountDao
 * @Author: xqg
 * @Date: 2018/12/1 0:00
 */
public interface IAdUserClickCountDao {
    /**
     * 批量更新（包括两步骤：①批量更新；②批量保存）
     */
    void updateBatch(List<AdUserClickCount> beans);
}
