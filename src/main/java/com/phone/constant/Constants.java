package com.phone.constant;

/**
 * @Description: TODO 常量
 * @ClassName: Constants
 * @Author: xqg
 * @Date: 2018/11/26 20:18
 */
public interface Constants {
    /**
     * 根据spark.job.deploy.mode 获取 resources\conf.properties 里面的信息
     * 获取部署模式local/test/production
     */
    String SPARK_JOB_DEPLOY_MODE = "spark.job.deploy.mode";
    /**
     * 数据库连接信息共通的资源文件名
     */
    String DBCP_COFIG_FILE = "dbcp.cofig.file";
}
