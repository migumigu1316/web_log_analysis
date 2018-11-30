package com.phone.util;

import com.phone.constant.Constants;
import com.phone.constant.DeployMode;

import java.io.IOException;
import java.util.Properties;

/**
 * 共同的资源文件
 * @Description: TODO 资源文件信息读取工具类
 * @ClassName: ResourcesUtils
 * @Author: xqg
 * @Date: 2018/11/26 20:19
 */

public class ResourcesUtils {
    private static Properties properties;

    //部署模式
    public static DeployMode dMode;


    static {
        properties = new Properties();
        try {
            //properties读取 resources(资源)/conf.properties文件中的"Spark作业的运行模式"
            properties.load(ResourcesUtils.class.getClassLoader().getResourceAsStream("conf.properties"));

            //动态设置部署模式,DeployMode枚举
            dMode = DeployMode.valueOf(getPropertyValueByKey(Constants.SPARK_JOB_DEPLOY_MODE).toUpperCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据key获得资源文件中的value
     *
     * @param key
     * @return
     */
    public static String getPropertyValueByKey(String key) {
        return properties.getProperty(key, "local");
    }
}
