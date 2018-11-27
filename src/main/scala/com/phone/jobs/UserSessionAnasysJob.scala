package com.phone.jobs

import com.phone.util.ResourcesUtils
import org.apache.spark.sql.SparkSession

/**
  *
  * @Description: TODO 用户Session分析模块
  * @ClassName: UserSessionAnasysJob
  * @Author: xqg
  * @Date: 2018/11/27 10:12
  *
  *  ~~~> 模块1：用户访问session分析
  *  步骤： （注意：模块下分多个功能）
  *  ①按条件筛选session
  *  ②统计出符合条件的session中，访问时长在1s~3s、4s~6s、7s~9s、10s~30s、30s~60s、1m~3m、3m~10m、10m~30m、30m以上各个范围内的session占比；访问步长在1~3、4~6、7~9、10~30、30~60、60以上各个范围内的session占比
  *  ③在符合条件的session中，按照时间比例随机抽取1000个session (等比例抽样，注意：总session数>1000，需要抽样；若<1000,以实际session数为准)
  *  ④在符合条件的session中，获取点击、下单和支付数量排名前10的品类
  *  ⑤对于排名前10的品类，分别获取其点击次数排名前10的session
  */
object UserSessionAnasysJob {
  def main(args: Array[String]): Unit = {
    //0.拦截非法的操作
    if(){

    }

    //1、SparkSession的实例，（注意：若分析的是hive表，builder到的实例.enableHiveSupport）
    val builder: SparkSession.Builder = SparkSession.builder().appName(UserSessionAnasysJob.getClass.getSimpleName)


    //若是本地模式，需要单独设置
    if (ResourcesUtils.dMode){

    }
  }
}
