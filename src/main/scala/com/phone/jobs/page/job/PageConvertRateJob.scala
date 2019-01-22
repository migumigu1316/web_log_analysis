package com.phone.jobs.page.job

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.phone.bean.common.{Task, TaskParam}
import com.phone.bean.page.PageSplitConvertRate
import com.phone.constant.Constants
import com.phone.dao.common.ITaskDao
import com.phone.dao.common.impl.TaskDaoImpl
import com.phone.dao.page.IPageSplitConvertRate
import com.phone.dao.page.impl.PageSplitConvertRateImpl
import com.phone.mock.MockData
import com.phone.util.{NumberUtils, ResourcesUtils, StringUtils}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession.Builder
import org.apache.spark.sql.{Row, SparkSession}

import scala.collection.mutable

/**
  *
  * @Description: TODO 页面单跳转化率
  * @ClassName: PageConvertRateJob
  * @Author: xqg
  * @Date: 2018/12/1 8:38
  *
  */

object PageConvertRateJob {
  def main(args: Array[String]): Unit = {
    //前提：
    val spark: SparkSession = prepareOperate(args)

    //步骤：
    //    1、按条件筛选session
    val tuple: (RDD[Row], java.util.List[Integer]) = filterSessionByCondition(spark, args)

    // 2、求页面单条转化率
    val page_flow_temp: String = calPageConvert(spark, args, tuple)

    //3，将结果保存到db中
    saveResultToDB(args, page_flow_temp)

    //4、资源释放
    spark.stop()
  }


  /**
    * 将结果保存到db中
    *
    * @param args
    * @param page_flow_temp
    */
  def saveResultToDB(args: Array[String], page_flow_temp: String) = {
    val bean: PageSplitConvertRate = new PageSplitConvertRate(args(0).toInt, page_flow_temp)
    val dao: IPageSplitConvertRate = new PageSplitConvertRateImpl
    dao.saveBeanToDB(bean)
  }

  /**
    * 2.求页面单条转化率
    *
    * @param spark
    * @param args
    */
  def calPageConvert(spark: SparkSession, args: Array[String], tuple: (RDD[Row], java.util.List[Integer])) = {
    val rdd: RDD[Row] = tuple._1
    val page_flow = tuple._2

    //前提：
    //①准备一个容器，用来存储每个页面的点击次数
    val container: mutable.Map[Int, Int] = new mutable.HashMap[Int, Int]()

    //②依次从页面流中取出每一个页面的编号，从RDD中与每个元素（页面id）进行比对,计算吻合的总次数，即为：当前页面点击的总数

    for (i <- 0 until page_flow.size()) {
      //页面id
      val page_id = page_flow.get(i)
      //当前页面点击总次数，根据page_id计算
      val nowPageTotalCnt = rdd.filter(row => {
        row.getAs[Integer]("page_id") == page_id
      }).count()

      //将当前页面访问的次数存入到容器中
      container.put(page_id, nowPageTotalCnt.toInt)
    }

    var page_flow_str: mutable.StringBuilder = new mutable.StringBuilder
    //  val PAGE_FLOW: String = "1_2=0|2_3=0|3_4=0|4_5=0|5_6=0|6_7=0|7_8=0|8_9=0|9_10=0"
    for (i <- 0 until page_flow.size() - 1) {
      val before = page_flow.get(i)
      val after = page_flow.get(i + 1)
      //页面流拼接成字符串
      page_flow_str.append(before).append("_").append(after).append(Constants.COMMON_INIT)
    }
    //"1_2=0|2_3=0|3_4=0|4_5=0|5_6=0|6_7=0|7_8=0|8_9=0|9_10=0|"用.deleteCharAt()去除最后的“|”符号
    page_flow_str = page_flow_str.deleteCharAt(page_flow_str.length - 1)

    var page_flow_temp = page_flow_str.toString()

    for (i <- 0 until page_flow.size() - 1) {
      //单前页面id
      val now_page_id = page_flow.get(i)
      //下一个页面id
      val next_page_id = page_flow.get(i + 1)
      //页面跳转字符串拼接，如：1_3
      val field = now_page_id + "_" + next_page_id
      //当前页面总数
      val now_page_id_cnt = container.get(now_page_id).get
      //下个页面总数
      val next_page_id_cnt = container.get(next_page_id).get

      var rate = 0.0
      if (now_page_id_cnt == 0 || next_page_id_cnt == 0) {
        rate = 0
      } else {
        //比率 = 后一个页面总数 / 当前页面总数    （调用数字换工具类，对小数类进行处理）
        rate = NumberUtils.formatDouble(next_page_id_cnt.toDouble / now_page_id_cnt, 5)
      }
      page_flow_temp = StringUtils.setFieldInConcatString(page_flow_temp, "\\|", field, rate + "")
    }

    //测试查看页面单跳转化率
//        println("page_flow_temp = " + page_flow_temp)
    //page_flow_temp = 1_2=0.4|2_5=2.75|5_6=0.63636|6_7=0.57143|7_8=1.75|8_66=1.28571|66_12=0.77778|12_23=1.57143
    //将结果返回给调用点
    page_flow_temp
  }

  /**
    * 准备操作
    *
    * @param args
    * @return
    */
  private def prepareOperate(args: Array[String]) = {
    //0、拦截非法的操作
    if (args == null || args.length != 1) {
      println("参数录入错误或是没有准备参数！请使用：spark-submit 主类  jar taskId")
      System.exit(-1)
    }

    //1、SparkSession的实例(注意：若分析的是hive表，需要启用对hive的支持，Builder的实例.enableHiveSupport())
    val builder: Builder = SparkSession.builder().appName(PageConvertRateJob.getClass.getSimpleName)

    //若是本地集群模式，需要单独设置
    if (ResourcesUtils.dMode.toString.toLowerCase().equals("local")) {
      builder.master("local[*]")
    }

    val spark: SparkSession = builder.getOrCreate()


    //2、将模拟的数据装载进内存（hive表中的数据）
    MockData.mock(spark.sparkContext, spark.sqlContext)

    //3、设置日志的显示级别
    spark.sparkContext.setLogLevel("WARN")


    //模拟数据测试：
    //spark.sql("select * from user_visit_action").show(1000)

    //4、返回SparkSession的实例
    spark
  }

  /**
    * 1.按条件筛选session
    *
    * @param spark
    * @param args
    */
  def filterSessionByCondition(spark: SparkSession, args: Array[String]) = {
    //①准备一个字符串构建器的实例StringBuffer，用于存储sql
    val buffer = new StringBuffer
    //user_visit_action 和 user_info 都在模拟数据中
    buffer.append("select u.page_id from user_visit_action u,user_info i where u.user_id=i.user_id ")

    //②根据从mysql中task表中的字段task_param查询到的值，进行sql语句的拼接
    val taskId = args(0).toInt
    val taskDao: ITaskDao = new TaskDaoImpl
    val task: Task = taskDao.findTaskById(taskId)

    // task_param={"ages":[18,30],"genders":["男"],"professionals":["教师", "工人", "记者", "演员", "厨师", "医生", "护士", "司机", "军人", "律师"],"cities":["南京", "无锡", "徐州", "常州", "苏州"],"key_words":["小米", "iPhone", "魅族", "荣耀", "OPPO", "vivo", "华为", "三星", "智能手环", "游戏手柄"],"start_time":"2018-12-02 00:00:00","end_time":"2018-12-02 23:23:23","page_flow":[1,2,5,6,7,8,66,12,23]}
    //task_param列的值，json串
    val taskParamJsonStr = task.getTask_param()

    //使用FastJson解析，将json对象格式的数据封装到实体类TaskParam中
    val taskParam: TaskParam = JSON.parseObject[TaskParam](taskParamJsonStr, classOf[TaskParam])

    //获得参数值
    val ages = taskParam.getAges//年龄
    val genders = taskParam.getGenders//性别
    val professionals = taskParam.getProfessionals//职业
    val cities = taskParam.getCities//城市

    //新增的条件
    val start_time = taskParam.getStart_time//开始时间
    val end_time = taskParam.getEnd_time//结束时间

    //后续计算的条件
    val page_flow = taskParam.getPage_flow//页面跳转流

    //ages
    if (ages != null && ages.size() > 0) {
      val minAge = ages.get(0)
      val maxAge = ages.get(1)
      buffer.append(" and i.age between ").append(minAge + "").append(" and ").append(maxAge + "")
    }

    //genders
    if (genders != null && genders.size() > 0) {
      //希望sql: ... and i.sex in('男','女')
      //JSON.toJSONString(genders, SerializerFeature.UseSingleQuotes)~> ['男','女']
      buffer.append(" and i.sex  in(").append(JSON.toJSONString(genders, SerializerFeature.UseSingleQuotes).replace("[", "").replace("]", "")).append(")")
    }

    //professionals
    if (professionals != null && professionals.size() > 0) {
      buffer.append(" and i.professional  in(").append(JSON.toJSONString(professionals, SerializerFeature.UseSingleQuotes).replace("[", "").replace("]", "")).append(")")
    }

    //cities
    if (cities != null && cities.size() > 0) {
      buffer.append(" and i.city in(").append(JSON.toJSONString(cities, SerializerFeature.UseSingleQuotes).replace("[", "").replace("]", "")).append(")")
    }

    //start_time
    if (start_time != null) {
      buffer.append(" and u.action_time>='").append(start_time).append("'")
    }

    //end_time
    if (end_time != null) {
      buffer.append(" and u.action_time<='").append(end_time).append("'")
    }

    //③测试，将结果转化为RDD,与计算条件一并返回给调用点
    (spark.sql(buffer.toString).rdd, page_flow)
  }
}
