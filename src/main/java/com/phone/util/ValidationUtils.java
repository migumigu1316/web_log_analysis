package com.phone.util;

/**
 * @Description: TODO 校验工具类
 * @ClassName: ResourcesUtils
 * @Author: xqg
 * @Date: 2018/11/26 20:19
 */

public class ValidationUtils {

    /**
     * 校验数据中的指定字段，是否在指定范围内
     *
     * @param data      数据
     * @param dataField 数据字段
     * @param parameter 参数
     * @return 校验结果
     */
    public static boolean between(String data, String dataField,
                                  String parameter, String delimiter) {
        return data != null && data.trim().equals(StringUtils.getFieldFromConcatString(parameter, delimiter, dataField));
    }
}
