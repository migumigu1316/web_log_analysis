package com.phone.dao.session;

import com.phone.bean.session.Top10CategorySession;

/**
 * @Description: TODO 存储top10每个品类的点击top10的session的数据访问层接口
 * @ClassName: ITop10CategorySession
 * @Author: xqg
 * @Date: 2018/11/30 20:48
 */
public interface ITop10CategorySession {
    /**
     * @param bean
     */
    void saveBeanToDB(Top10CategorySession bean);
}
