package com.phone.dao.session;

import com.phone.bean.session.SessionDetail;

/**
 * @Description: TODO 操作session的明细数据数据访问层的接口
 * @ClassName: ISessionDetail
 * @Author: xqg
 * @Date: 2018/11/29 22:43
 */
public interface ISessionDetail {
    /**
     * 将SessionDetail 实例保存到db中
     * @param bean
     */
    void saveToDB(SessionDetail bean);
}
