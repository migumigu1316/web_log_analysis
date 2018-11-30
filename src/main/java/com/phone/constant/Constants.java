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
     * 获取spark的job的deploy(部署)mode(模式) local/test/production
     */
    String SPARK_JOB_DEPLOY_MODE = "spark.job.deploy.mode";
    /**
     * 数据库连接信息共通的资源文件名
     */
    String DBCP_COFIG_FILE = "dbcp.cofig.file";

    /**
     * 共通初始化
     */
    String COMMON_INIT = "=0|";//初始值
    String COMMON_INIT_2 = "=0";//结尾值

    /**
     * 不同session数的标识 （去重之后的session总数）
     */
    String SESSION_COUNT = "session_count";

    /**
     * 时长标识
     */
    String TIME_PERIOD_1s_3s = "1s_3s";
    String TIME_PERIOD_4s_6s = "4s_6s";
    String TIME_PERIOD_7s_9s = "7s_9s";
    String TIME_PERIOD_10s_30s = "10s_30s";
    String TIME_PERIOD_30s_60s = "30s_60s";
    String TIME_PERIOD_1m_3m = "1m_3m";
    String TIME_PERIOD_3m_10m = "3m_10m";
    String TIME_PERIOD_10m_30m = "10m_30m";
    String TIME_PERIOD_30m = "30m";

    /**
     * 步长标识
     */
    String STEP_PERIOD_1_3 = "1_3";
    String STEP_PERIOD_4_6 = "4_6";
    String STEP_PERIOD_7_9 = "7_9";
    String STEP_PERIOD_10_30 = "10_30";
    String STEP_PERIOD_30_60 = "30_60";
    String STEP_PERIOD_60 = "60";

    /**
     * session聚合统计的结果常量
     *
     * session_count=0|1s_3s=0|4s_6s=0|...|60=0
     *
     * 设想：经过累加器不断操作后，值最终形式 如：
     *
     * session_count=100|1s_3s=4|4s_6s=3|...|60=9
     */
    StringBuilder AGGR_RESULT = new StringBuilder()
            .append(SESSION_COUNT).append(COMMON_INIT)
            .append(TIME_PERIOD_1s_3s).append(COMMON_INIT)
            .append(TIME_PERIOD_4s_6s).append(COMMON_INIT)
            .append(TIME_PERIOD_7s_9s).append(COMMON_INIT)
            .append(TIME_PERIOD_10s_30s).append(COMMON_INIT)
            .append(TIME_PERIOD_30s_60s).append(COMMON_INIT)
            .append(TIME_PERIOD_1m_3m).append(COMMON_INIT)
            .append(TIME_PERIOD_3m_10m).append(COMMON_INIT)
            .append(TIME_PERIOD_10m_30m).append(COMMON_INIT)
            .append(TIME_PERIOD_30m).append(COMMON_INIT)
            .append(STEP_PERIOD_1_3).append(COMMON_INIT)
            .append(STEP_PERIOD_4_6).append(COMMON_INIT)
            .append(STEP_PERIOD_7_9).append(COMMON_INIT)
            .append(STEP_PERIOD_10_30).append(COMMON_INIT)
            .append(STEP_PERIOD_30_60).append(COMMON_INIT)
            .append(STEP_PERIOD_60).append(COMMON_INIT_2);
}
