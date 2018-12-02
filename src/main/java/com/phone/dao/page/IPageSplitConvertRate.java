package com.phone.dao.page;

import com.phone.bean.page.PageSplitConvertRate;

/**
 * @Description: TODO 页面单跳转化率数据访问层接口
 * @ClassName: IPageSplitConvertRate
 * @Author: xqg
 * @Date: 2018/11/30 22:27
 */
public interface IPageSplitConvertRate {
    /**
     * 将页面单跳转化率结果保存到db中
     *
     * @param bean
     */
    void saveBeanToDB(PageSplitConvertRate bean);
}
