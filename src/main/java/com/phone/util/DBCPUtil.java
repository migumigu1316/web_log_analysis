package com.phone.util;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

import com.phone.constant.Constants;
import com.phone.constant.DeployMode;
import org.apache.commons.dbcp.BasicDataSourceFactory;

public class DBCPUtil {
    private static DataSource ds;

    static {
        try {
            InputStream in = null;
            //用来读取xxx.properties文件配置
            Properties properties = new Properties();
            /**
             * 根据conf.properties中的配置信息来判断是执行本地/测试还是生产环境
             */
            DeployMode runMode = ResourcesUtils.dMode;

            /**
             * 动态的获取文件的目录
             */
            //备注：File.separator是目录的分隔符，系统不一样分隔符就不一样
            String filePath = runMode.toString().toLowerCase() + File.separator + ResourcesUtils.getPropertyValueByKey(Constants.DBCP_COFIG_FILE);
//            System.out.println("filePath = " + filePath);

            in = DBCPUtil.class.getClassLoader().getResourceAsStream(filePath);
            //properties加载文件路径
            properties.load(in);

            //连接池的实例
            ds = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {//初始化异常
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 获得数据源（连接池）
     *
     * @return
     */
    public static DataSource getDataSource() {
        return ds;
    }

    /**
     * 从连接池中获得连接的实例
     *
     * @return
     */
    public static Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
