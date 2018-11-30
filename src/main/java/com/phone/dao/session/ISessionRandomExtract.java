package com.phone.dao.session;

import com.phone.bean.session.SessionRandomExtract;

import java.util.List;

/**
 * @Description: TODO 按时间比例随机抽取功能抽取出来的session 数据访问层接口
 * @ClassName: ISessionRandomExtract
 * @Author: xqg
 * @Date: 2018/11/29 14:34
 */
public interface ISessionRandomExtract {
    /**
     * 将容器中所有的实体批量保存到db中
     * @param beans
     */
    void saveBeansToDB(List<SessionRandomExtract> beans);
}
