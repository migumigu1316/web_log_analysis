package com.phone.dao.common;

import com.phone.bean.common.Task;

/**
 * @Description: TODO 任务表Task数据访问操作
 * @ClassName: ITaskDao
 * @Author: xqg
 * @Date: 2018/11/27 21:05
 */
public interface ITaskDao {
    /**
     * 根据任务id查询相应的Task信息
     * @param taskId
     * @return
     */
    Task findTaskById(int taskId);
}
