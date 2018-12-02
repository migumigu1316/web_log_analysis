package com.phone.dao.ad;

import com.phone.bean.ad.AdProvinceTop3;

import java.util.List;

/**
 * @Description: TODO 每天各省份top3热门广告的数据处理Dao层接口
 * @ClassName: IAdProvinceTop3Dao
 * @Author: xqg
 * @Date: 2018/12/1 0:03
 */
public interface IAdProvinceTop3Dao {
    /**
     * 批量更新
     */
    void updateBatch(List<AdProvinceTop3> beans);
}
