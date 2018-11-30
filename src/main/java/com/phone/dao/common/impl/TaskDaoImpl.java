package com.phone.dao.common.impl;

import com.phone.bean.common.Task;
import com.phone.dao.common.ITaskDao;
import com.phone.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

/**
 * @Description: TODO 任务表Task数据访问操作实现类
 * @ClassName: TaskDaoImpl
 * @Author: xqg
 * @Date: 2018/11/27 21:15
 */
public class TaskDaoImpl implements ITaskDao {
    //获取连接池的实例
    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    @Override
    public Task findTaskById(int taskId) {
        try {
            return qr.query("select * from task where task_id = ?", new BeanHandler<>(Task.class), taskId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
