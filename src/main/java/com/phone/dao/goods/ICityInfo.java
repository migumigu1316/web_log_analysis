package com.phone.dao.goods;

import com.phone.bean.goods.CityInfo;

import java.util.List;

/**
 * @Description: TODO 操作城市信息数据访问层接口
 * @ClassName: ICityInfo
 * @Author: xqg
 * @Date: 2018/11/30 22:29
 */
public interface ICityInfo {
    /**
     * 查询所有的城市信息
     *
     * @return
     */
    List<CityInfo> findAllInfos();
}
