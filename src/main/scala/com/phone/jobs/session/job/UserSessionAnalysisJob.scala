package com.phone.jobs.session.job

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.phone.bean.common.{Task, TaskParam}
import com.phone.bean.session.{SessionAggrStat, SessionDetail, SessionRandomExtract}
import com.phone.constant.Constants
import com.phone.dao.common.ITaskDao
import com.phone.dao.common.impl.TaskDaoImpl
import com.phone.dao.session.impl.{SessionAggrStatImpl, SessionDetailImpl, SessionRandomExtractImpl}
import com.phone.dao.session.{ISessionAggrStat, ISessionDetail, ISessionRandomExtract}
import com.phone.jobs.session.accumulator.SessionAggrStatAccumulator
import com.phone.jobs.session.job.UserSessionAnalysisJob.{aggrResultToDB, extractSessionByRate, randomSessionToDetail}
import com.phone.mock.MockData
import com.phone.util.{ResourcesUtils, StringUtils}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.collection.mutable.ArrayBuffer

/**
  *
  * @Description: TODO 用户Session分析模块
  * @ClassName: UserSessionAnalysisJob
  * @Author: xqg
  * @Date: 2018/11/27 10:12
  *
  *        ~~~> 模块1：用户访问session分析
  *        步骤： （注意：模块下分多个功能）
  *        ①按条件筛选session
  *        ②统计出符合条件的session中，访问时长在1s~3s、4s~6s、7s~9s、10s~30s、30s~60s、1m~3m、3m~10m、10m~30m、30m以上各个范围内的session占比；访问步长在1~3、4~6、7~9、10~30、30~60、60以上各个范围内的session占比
  *        ③在符合条件的session中，按照时间比例随机抽取1000个session (等比例抽样，注意：总session数>1000，需要抽样；若<1000,以实际session数为准)
  *        ④在符合条件的session中，获取点击、下单和支付数量排名前10的品类
  *        ⑤对于排名前10的品类，分别获取其点击次数排名前10的session
  */
object UserSessionAnalysisJob {

  def main(args: Array[String]): Unit = {
    //前提:前期准备
    val spark: SparkSession = prepareOperate(args) //prepareOperate准备操作

    //步骤:
    /**
      * 1.按条件筛选session
      */
    filterSessionByCondition(spark, args)

    /**
      * 2、统计出符合条件的session中，访问时长在1s~3s、4s~6s、7s~9s、10s~30s、30s~60s、1m~3m、3m~10m、
      * 10m~30m、30m以上各个范围内的session占比；访问步长在1~3、4~6、7~9、10~30、30~60、60以上各个
      * 范围内的session占比
      */
//    getStepLenAndTimeLenRate(spark, args)

    /**
      * 3、在符合条件的session中，按照时间比例随机抽取1000个session
      */
    randomExtract1000Session(spark, args)

    /**
      * 4、在符合条件的session中，获取点击、下单和支付数量排名前10的品类
      */


    /**
      * 5、对于排名前10的品类，分别获取其点击次数排名前10的session
      */
  }

  //---------------------------------☆☆☆方法☆☆☆---------------------------------------


  /**
    * 3. 在符合条件的session中，按照时间比例随机抽取1000个session
    *
    * @param spark
    * @param args
    */
  def randomExtract1000Session(spark: SparkSession, args: Array[String]) = {
    //  select substring(action_time,1,13) ,count(*) totalSessionCnt from filter_after_action group by  substring(action_time,1,13) ~>求出各个时段内的session总数
    //  直接对临时表filter_after_action求总session数
    // 1.准备一个存储sessionId的容器
    val container: ArrayBuffer[String] = new ArrayBuffer()
    //广播sessionId
    val bcContainer: Broadcast[ArrayBuffer[String]] = spark.sparkContext.broadcast[ArrayBuffer[String]](container)

    // ①求出每个时间段内的session数占总session数的比例值（不去重的session数）
    //总session数
    val totalSessionCnt: Long = spark.sql("select count(*) totalSessionCnt from filter_after_action ").first.getAs[Long]("totalSessionCnt")
    val rdd: RDD[Row] = spark.sql(s"select substring(action_time,1,13) timePeriod ,count(*) / ${totalSessionCnt.toDouble} rateValue from filter_after_action group by substring(action_time,1,13)").rdd
    //    spark.sql(s"select substring(action_time,1,13) timePeriod ,count(*) / ${totalSessionCnt.toDouble} rateValue from filter_after_action group by substring(action_time,1,13)").show(1000)
    /**
      * 时间段       比率
      * +-------------+---------+
      * |   timePeriod|rateValue|
      * +-------------+---------+
      * |2018-11-29 19| 0.056005|
      * |2018-11-29 09| 0.038972|
      * |2018-11-29 01| 0.012413|
      */

    //广播task_id,将TaskId封装到广播变量中
    val bcTaskId: Broadcast[Int] = spark.sparkContext.broadcast[Int](args(0).toInt)
    //②根据比例值rdd，从指定的时段内随机抽取相应数量的session,并变形后保存到db中
    //循环分析
    rdd.collect.foreach(row => {
      //根据比率值从filter_after_action抽取session
      extractSessionByRate(row, spark, totalSessionCnt)

      //将结果映射为一张临时表，聚合后保存到db中
      aggrResultToDB(spark, bcTaskId, bcContainer)
    })

    //③向存储随机抽取出来的session的明细表中存储数据
    //容器中存取的session_id与filter_after_action表进行内连接查询，查询处满足条件的记录保存到明细表中
    randomSessionToDetail(spark, bcTaskId, bcContainer)
  }

  /**
    * 3.3 向存储随机抽取出来的session的明细表中存储数据
    *
    * @param spark
    * @param bcTaskId
    * @param bcContainer
    */
  private def randomSessionToDetail(spark: SparkSession, bcTaskId: Broadcast[Int], bcContainer: Broadcast[ArrayBuffer[String]]) = {
    //③向存储随机抽取出来的session的明细表中存取数据
    //将容器中存取的session_id与filter_after_action表进行内连接查询，查询处满足条件的记录保存到明细表中

    //将容器映射为一张临时表，与filter_after_action表进行内连接查询
    //container容器中存储的是sessionId
    val rddContainer: RDD[Row] = spark.sparkContext.parallelize(bcContainer.value).map(perEle => Row(perEle))
    val structTypeContainer = StructType(Seq(StructField("session_id", StringType, true)))
    //创建df
    val df2: DataFrame = spark.createDataFrame(rddContainer, structTypeContainer)
    //创建临时表
    df2.createOrReplaceTempView("container_temp")

    //dao层,session下的 接口类,实现接口类; bean层session下的实体类
    val dao: ISessionDetail = new SessionDetailImpl

    val rddSD: RDD[Row] = spark.sql("select * from container_temp t,filter_after_action f where t.session_id=f.session_id").rdd
    //循环,获取字段
    rddSD.collect.foreach(row => {
      val task_id = bcTaskId.value
      val user_id = row.getAs[Long]("user_id").toInt
      val session_id = row.getAs[String]("session_id")
      val page_id = row.getAs[Long]("page_id").toInt
      val action_time = row.getAs[String]("action_time")
      val search_keyword = row.getAs[String]("search_keyword")
      val click_category_id = row.getAs[Long]("click_category_id").toInt
      val click_product_id = row.getAs[Long]("click_product_id").toInt
      val order_category_ids = row.getAs[String]("order_category_ids")
      val order_product_ids = row.getAs[String]("order_product_ids")
      val pay_category_ids = row.getAs[String]("pay_category_ids")
      val pay_product_ids = row.getAs[String]("pay_product_ids")

      //将字段放到SessionDetail实体类中
      val bean = new SessionDetail(task_id, user_id, session_id, page_id, action_time, search_keyword, click_category_id, click_product_id, order_category_ids, order_product_ids, pay_category_ids, pay_product_ids)
      //将SessionDetail实体类中
      dao.saveToDB(bean)
    })
  }

  /**
    * 3.2 将结果映射为一张临时表，聚合后保存到db中
    *
    * @param spark
    * @param bcTaskId
    * @param bcContainer
    */
  private def aggrResultToDB(spark: SparkSession, bcTaskId: Broadcast[Int], bcContainer: Broadcast[ArrayBuffer[String]]) = {
    //从临时表temp_random中选出最后结果保存到DB中
    //将结果落地到mysql相应的表中，需要通过一个容器将sesssion_id存储一份,将上述的结果映射为一张临时表，进行聚合操作
    //TODO  concat_ws,第一个参数是其它参数的分隔符。分隔符的位置放在要连接的两个字符串之间。和 collect_set()（对某列进行去重）
    //concat_ws,collect_set,搞明白sql语句中用到的函数
    val nowPeriodAllSessionRDD: RDD[Row] = spark.sql(" select  session_id,concat_ws(',', collect_set(distinct search_keyword)) search_keywords ,min(action_time) start_time,max(action_time) end_time from temp_random group by session_id").rdd

    nowPeriodAllSessionRDD.foreachPartition(itr => {
      if (!itr.isEmpty) {
        //取出最后DB库中相应表的字段,放到一个集合中,定义一个集合List<SessionRandomExtract>
        val beans: util.List[SessionRandomExtract] = new util.LinkedList()
        //循环取出相应字段的值
        itr.foreach(t => {
          val task_id: Int = bcTaskId.value
          //广播的task_id
          val session_id: String = t.getAs[String]("session_id")
          val start_time: String = t.getAs[String]("start_time")
          val end_time: String = t.getAs[String]("end_time")
          val search_keywords: String = t.getAs[String]("search_keywords")

          //将取出的字段放到定义好的List<SessionRandomExtract>中
          val bean = new SessionRandomExtract(task_id, session_id, start_time, end_time, search_keywords)

          beans.add(bean)

          //向容器中存入当前的session_id
          bcContainer.value.append(session_id)
        })

        //准备dao层的实例ISessionRandomExtract
        val dao: ISessionRandomExtract = new SessionRandomExtractImpl

        //调用接口中的saveBeansToDB方法,将当前集合中的所有实例保存到表中
        dao.saveBeansToDB(beans)
      }
    })
  }

  /**
    * 3.1 根据比率值从filter_after_action抽取session
    *
    * @param row
    * @param spark
    * @param totalSessionCnt
    */
  def extractSessionByRate(row: Row, spark: SparkSession, totalSessionCnt: Long) = {
    //根据比率*总session数,获取指定时间段内的session数
    val nowTimePeriod = row.getAs[String]("timePeriod")
    //当前时间段
    //其实java的float只能用来进行科学计算或工程计算，在大多数的商业计算中，一般采用java.math.BigDecimal类来进行精确计算。
    val rateValue = row.getAs[java.math.BigDecimal]("rateValue").doubleValue()
    //比率值
    //需要的session数
    val needTotalSessionCnt = if (totalSessionCnt > 1000) 1000 else totalSessionCnt

    //备注:select instr('helloworld','l',4,2) from dual;  --返回结果：9    也就是说：在"helloworld"的第4(l)号位置开始，查找第二次出现的“l”的位置
    //instr(action_time,$nowTimePeriod) > 0,在action_time字段中,出现nowTimePeriod列中的字段,默认第一次出现的位置
    //TODO 用instr函数时注意:instr('helloworld','l'),第二个参数的引号必须加上,不然报sql语句解析异常
    val df1: DataFrame = spark.sql(s"select session_id,action_time,search_keyword from filter_after_action where instr(action_time,'$nowTimePeriod') > 0")

    //查看
    //spark.sql(s"select session_id,action_time,search_keyword from filter_after_action where instr(action_time,'$nowTimePeriod') > 0").show(1000)

    //进行抽样,1000个session
    val arr: Array[Row] = df1.rdd.takeSample(true, (needTotalSessionCnt * rateValue).toInt)

    //为创建临时表做准备,RDD[row] 和 StructType
    val rdd: RDD[Row] = spark.sparkContext.parallelize(arr)
    val structType: StructType = StructType(Seq(StructField("session_id", StringType, false),
      StructField("action_time", StringType, false),
      StructField("search_keyword", StringType, true)))
    //创建临时表
    val df: DataFrame = spark.createDataFrame(rdd, structType)
    df.createOrReplaceTempView("temp_random") //创建临时表:表名<临时随机表>

    /**
      * +----------+-------+--------------------+-------+-------------------+--------------+-----------------+----------------+------------------+-----------------+----------------+---------------+-------+-----------+----+---+------------+----+---+
      * |      date|user_id|          session_id|page_id|        action_time|search_keyword|click_category_id|click_product_id|order_category_ids|order_product_ids|pay_category_ids|pay_product_ids|user_id|   username|name|age|professional|city|sex|
      * +----------+-------+--------------------+-------+-------------------+--------------+-----------------+----------------+------------------+-----------------+----------------+---------------+-------+-----------+----+---+------------+----+---+
      * |2018-11-29|     26|8f838a0a32d44b19a...|     18|2018-11-29 21:46:22|          null|               84|              79|              null|             null|            null|           null|     26|       Ella|  祯初| 40|          律师|  南京|  男|
      * |2018-11-29|     26|8f838a0a32d44b19a...|     77|2018-11-29 21:24:57|          null|             null|            null|                31|               39|            null|           null|     26|       Ella|  祯初| 40|          律师|  南京|  男|
      */
  }

  /**
    * 2、统计出符合条件的session中，访问时长在1s~3s、4s~6s、7s~9s、10s~30s、30s~60s、1m~3m、3m~10m、
    * 10m~30m、30m以上各个范围内的session占比；访问步长在1~3、4~6、7~9、10~30、30~60、60以上各个
    * 范围内的session占比
    *
    * @param spark
    */
  private def getStepLenAndTimeLenRate(spark: SparkSession, args: Array[String]) = {
    //select count(*) stepLen, 自定义函数(结束时间，开始时间) timeLen from filter_after_action group by session_id;
    //TODO 注册自定义函数
    spark.udf.register("getTimeLen", (endTime: String, startTime: String) => getTimeLen(endTime, startTime))
    //求出各个session的步长和时长，根据session_id进行分组
    //    spark.sql("select count(*) stepLen,getTimeLen(max(action_time),min(action_time)) " +
    //      "timeLen from filter_after_action group by session_id").show(1000)//测试
    val rdd: RDD[Row] = spark.sql("select count(*) stepLen,getTimeLen(max(action_time),min(action_time)) " +
      "timeLen from filter_after_action group by session_id").rdd

    //TODO (☆☆☆☆☆)准备一个自定义累加器的实例，并进行注册
    val acc: SessionAggrStatAccumulator = new SessionAggrStatAccumulator
    spark.sparkContext.register(acc)

    //将虚拟表转换成rdd,通过循环分析rdd
    rdd.collect.foreach(row => {
      //在这里使用collect可能会导致OOM
      //循环一次rdd,session_count累加1
      acc.add(Constants.SESSION_COUNT)

      //2.1将当前的步长与各个步长进行比对，若吻合，当前的步长累加1
      calStepLenSessionCnt(acc, row) //计算session步长的总数

      //2.2将当前的时长与各个时长进行比对，若吻合，当前的时长累加1
      calTimeLenSessionCnt(acc, row) //计算session时长的总数

    })
    //2.3 将最终的结果保存到db中的session_aggr_stat表
    saveSessionAggrStatToDB(args, acc) //session数据保存到DB中
  }

  /**
    * 2.3 将最终的结果保存到db中的session_aggr_stat表
    *
    * @param args
    * @param acc
    */
  private def saveSessionAggrStatToDB(args: Array[String], acc: SessionAggrStatAccumulator) = {
    //将结果保存到db中,保存到session_aggr_stat中
    val finalResult = acc.value
    //各个时间段内的session数/总session数
    val session_count = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.SESSION_COUNT).toInt
    val period_1s_3s = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_1s_3s).toDouble / session_count
    val period_4s_6s = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_4s_6s).toDouble / session_count
    val period_7s_9s = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_7s_9s).toDouble / session_count
    val period_10s_30s = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_10s_30s).toDouble / session_count
    val period_30s_60s = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_30s_60s).toDouble / session_count
    val period_1m_3m = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_1m_3m).toDouble / session_count
    val period_3m_10m = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_3m_10m).toDouble / session_count
    val period_10m_30m = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_10m_30m).toDouble / session_count
    val period_30m = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.TIME_PERIOD_30m).toDouble / session_count
    //各个步长范围内的session数/总session数
    val step_1_3 = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.STEP_PERIOD_1_3).toDouble / session_count
    val step_4_6 = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.STEP_PERIOD_4_6).toDouble / session_count
    val step_7_9 = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.STEP_PERIOD_7_9).toDouble / session_count
    val step_10_30 = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.STEP_PERIOD_10_30).toDouble / session_count
    val step_30_60 = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.STEP_PERIOD_30_60).toDouble / session_count
    val step_60 = StringUtils.getFieldFromConcatString(finalResult, "\\|", Constants.STEP_PERIOD_60).toDouble / session_count

    val bean: SessionAggrStat = new SessionAggrStat(args(0).toInt, session_count, period_1s_3s, period_4s_6s, period_7s_9s, period_10s_30s, period_30s_60s, period_1m_3m, period_3m_10m, period_10m_30m, period_30m, step_1_3, step_4_6, step_7_9, step_10_30, step_30_60, step_60)
    //    println(finalResult)
    //session_count=2|1s_3s=0|4s_6s=0|7s_9s=0|10s_30s=0|30s_60s=0|1m_3m=0|3m_10m=0|10m_30m=0|30m=0|1_3=1|4_6=1|7_9=0|10_30=0|30_60=0|60=0
    //写代码,碰见什么写什么,如下面的步骤,①写bean层(实体类,如get,set),②写dao层(接口,连接数据库,不涉及逻辑),③写业务逻辑层
    val dao: ISessionAggrStat = new SessionAggrStatImpl
    dao.saveBeanToDB(bean)
  }

  /**
    * 2.2 求相应时长范围内的session数
    *
    * @param acc
    * @param row
    */
  private def calTimeLenSessionCnt(acc: SessionAggrStatAccumulator, row: Row) = {
    //将当前的时长与各个时长进行比对，若吻合，当前的时长累加1
    val nowTimeLen: Long = row.getAs[Long]("timeLen")
    val timeLenSeconds = nowTimeLen / 1000 //毫秒转成秒
    val timeLenMinutes = timeLenSeconds / 60 //秒转成分钟

    if (timeLenSeconds >= 1 && timeLenSeconds <= 3) {
      acc.add(Constants.TIME_PERIOD_1s_3s)
    } else if (timeLenSeconds >= 4 && timeLenSeconds <= 6) {
      acc.add(Constants.TIME_PERIOD_4s_6s)
    } else if (timeLenSeconds >= 7 && timeLenSeconds <= 9) {
      acc.add(Constants.TIME_PERIOD_7s_9s)
    } else if (timeLenSeconds >= 10 && timeLenSeconds <= 30) {
      acc.add(Constants.TIME_PERIOD_10s_30s)
    } else if (timeLenSeconds > 30 && timeLenSeconds < 60) {
      acc.add(Constants.TIME_PERIOD_30s_60s)
    } else if (timeLenMinutes >= 1 && timeLenMinutes < 3) {
      acc.add(Constants.TIME_PERIOD_1m_3m)
    } else if (timeLenMinutes >= 3 && timeLenMinutes < 10) {
      acc.add(Constants.TIME_PERIOD_3m_10m)
    } else if (timeLenMinutes >= 10 && timeLenMinutes < 30) {
      acc.add(Constants.TIME_PERIOD_10m_30m)
    } else if (timeLenMinutes >= 30) {
      acc.add(Constants.TIME_PERIOD_30m)
    }
  }

  /**
    * 2.1 求session在相应步长中的个数
    *
    * @param acc
    * @param row
    */
  private def calStepLenSessionCnt(acc: SessionAggrStatAccumulator, row: Row) = {
    //将当前的步长与各个步长进行比对，若吻合，当前的步长累加1
    val nowStepLen: Long = row.getAs[Long]("stepLen")
    if (nowStepLen >= 1 && nowStepLen <= 3) {
      acc.add(Constants.STEP_PERIOD_1_3)
    } else if (nowStepLen >= 4 && nowStepLen <= 6) {
      acc.add(Constants.STEP_PERIOD_4_6)
    } else if (nowStepLen >= 7 && nowStepLen <= 9) {
      acc.add(Constants.STEP_PERIOD_7_9)
    } else if (nowStepLen >= 10 && nowStepLen <= 30) {
      acc.add(Constants.STEP_PERIOD_10_30)
    } else if (nowStepLen > 30 && nowStepLen <= 60) {
      acc.add(Constants.STEP_PERIOD_30_60)
    } else if (nowStepLen > 60) {
      acc.add(Constants.STEP_PERIOD_60)
    }
  }

  /**
    * 1. 按条件筛选session
    * Condition条件
    *
    * @param spark
    * @param args
    */
  def filterSessionByCondition(spark: SparkSession, args: Array[String]) = {

    //准备一个字符串构建器的实例StringBuffer,用来存储sql
    val buffer = new StringBuffer
    //sql查询语句,后面拼接
    buffer.append("select u.session_id,u.action_time,u.search_keyword,i.user_id,u.page_id,u.click_category_id,u.click_product_id,u.order_category_ids,u.order_product_ids,u.pay_category_ids,u.pay_product_ids from  user_visit_action u,user_info i where u.user_id=i.user_id ")

    //根据mysql中task表中的task_param列查询出结果,进行SQL语句的拼接
    val taskId: Int = args(0).toInt
    //dao层
    val taskDao: ITaskDao = new TaskDaoImpl
    val task: Task = taskDao.findTaskById(taskId)
    //    Task(task_id=1, task_name=用户访问session分析, create_time=null, start_time=null, finish_time=null, task_type=用户访问session分析任务, task_status=null, task_param={"ages":[0,100],"genders":["男","女"],"professionals":["教师", "工人", "记者", "演员", "厨师", "医生", "护士", "司机", "军人", "律师"],"cities":["南京", "无锡", "徐州", "常州", "苏州", "南通", "连云港", "淮安", "盐城", "扬州"]})

    //获取 task_param里面的值,做一个集合类专门用来存储
    val taskParamJsonStr = task.getTask_param();

    //    println(taskParamJsonStr)
    //TODO 使用FastJson,将json对象格式的数据封装到实体类TaskParam中
    val taskParam: TaskParam = JSON.parseObject[TaskParam](taskParamJsonStr, classOf[TaskParam])
    //    println(taskParam)

    //获得参数值
    val ages: util.List[Integer] = taskParam.getAges
    val genders: util.List[String] = taskParam.getGenders
    val professionals: util.List[String] = taskParam.getProfessionals
    val cities: util.List[String] = taskParam.getCities

    //前提：将字段task_param中的值已经封装到了实例CommonCondition中了（字段：ages年龄,genders性别,professionals职业,cities城市）
    //拼接需要查询ages的条件
    if (ages != null && ages.size() > 0) {
      val minAge = ages.get(0)
      val maxAge = ages.get(1)
      //拼接sql的查询条件,
      // "select * from  user_visit_action u,user_info i where u.user_id = i.user_id
      // and i.age between minAge and maxAge
      buffer.append(" and i.age between ").append(minAge + "").append(" and ").append(maxAge + "")
    }

    //genders
    //TODO append(JSON.toJSONString(cities, SerializerFeature.UseSingleQuotes) 是什么意思???
    if (genders != null && genders.size() > 0) {
      //希望sql: ... and i.sex in('男','女')
      buffer.append(" and i.sex in (").append(JSON.toJSONString(genders,
        SerializerFeature.UseSingleQuotes).replace("[", "").replace("]", "")).append(")")
    }

    //cities
    if (cities != null && cities.size() > 0) {
      buffer.append(" and i.city in (").append(JSON.toJSONString(cities,
        SerializerFeature.UseSingleQuotes).replace("[", "").replace("]", "")).append(")")
    }

    //测试，然后将结果注册为一张临时表，供后续的步骤使用（为了提高速度：需要将临时表缓存起来）
    //    println("sql语句："+buffer.toString)
    ////    sql语句：select * from  user_visit_action u,user_info i where u.user_id = i.user_id  and i.age between 0 and 100 and i.sex in ('男','女') and i.city in ('南京','无锡','徐州','常州','苏州','南通','连云港','淮安','盐城','扬州')
    //    spark.sql(buffer.toString).show(2000)

    //创建临时表
    spark.sql(buffer.toString).createTempView("filter_after_action")

    //缓存表,后续操作这个缓存表
    spark.sqlContext.cacheTable("filter_after_action")
    //        spark.sql("select * from filter_after_action").show(1000)

    /**
      * +----------+-------+--------------------+-------+-------------------+--------------+-----------------+----------------+------------------+-----------------+----------------+---------------+-------+-----------+----+---+------------+----+---+
      * |      date|user_id|          session_id|page_id|        action_time|search_keyword|click_category_id|click_product_id|order_category_ids|order_product_ids|pay_category_ids|pay_product_ids|user_id|   username|name|age|professional|city|sex|
      * +----------+-------+--------------------+-------+-------------------+--------------+-----------------+----------------+------------------+-----------------+----------------+---------------+-------+-----------+----+---+------------+----+---+
      * |2018-11-29|     26|8f838a0a32d44b19a...|     18|2018-11-29 21:46:22|          null|               84|              79|              null|             null|            null|           null|     26|       Ella|  祯初| 40|          律师|  南京|  男|
      * |2018-11-29|     26|8f838a0a32d44b19a...|     77|2018-11-29 21:24:57|          null|             null|            null|                31|               39|            null|           null|     26|       Ella|  祯初| 40|          律师|  南京|  男|
      */
  }

  /**
    * 1. 前期准备操作
    *
    * @param args
    * @return
    */
  private def prepareOperate(args: Array[String]) = {
    //0.拦截非法的操作
    if (args == null || args.length != 1) {
      println("参数录入错误或者没有准备参数!!! 请使用:spark-submit 主类 jar taskId")
      System.exit(-1)
    }

    //1、SparkSession的实例，（注意：若分析的是hive表，builder的实例.enableHiveSupport）
    val builder: SparkSession.Builder = SparkSession.builder().appName(UserSessionAnalysisJob.getClass.getSimpleName)


    //若是本地模式，需要单独设置
    if (ResourcesUtils.dMode.toString.toLowerCase.equals("local")) {
      builder.master("local[*]")
    }

    val spark: SparkSession = builder.getOrCreate()

    //2.将模拟的数据装载到内存,(hive表中的数据)
    MockData.mock(spark.sparkContext, spark.sqlContext)

    //3.设置日志的显示级别
    spark.sparkContext.setLogLevel("WARN")

    //模拟数据测试
    //spark.sql("select * from user_visit_action").show(1000)

    //方法抽取,最后返回SparkSession的实例
    spark
  }

  /**
    * 获得时长值
    *
    * @param startTime 如: 2018-11-27 23:54
    * @param endTime
    * @return
    */
  def getTimeLen(startTime: String, endTime: String): Long = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.parse(endTime).getTime - sdf.parse(startTime).getTime
  }

}
