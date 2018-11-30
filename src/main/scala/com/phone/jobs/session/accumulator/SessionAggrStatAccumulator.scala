package com.phone.jobs.session.accumulator

import com.phone.constant.Constants
import com.phone.util.StringUtils
import org.apache.spark.util.AccumulatorV2

/**
  * Description：自定义的累加器，用于记录session数据中各个统计维度出现的session的数量
  * 比如session时长：
  * 1s_3s=5
  * 4s_6s=8
  *
  * 比如session步长：
  * 1_3=5
  * 4_6=10
  *
  * 这种自定义的方式在工作过程中非常有用，可以避免写大量的transformation操作
  * 在scala中自定义累加器，需要继承AccumulatorV2（spark旧版本需要继承AccumulatorParam）这样一个抽象类
  *
  * @Description: TODO 自定义时长累加器
  * @ClassName: SessionAggrStatAccumulator
  * @Author: xqg
  * @Date: 2018/11/28 0:35
  *
  */
class SessionAggrStatAccumulator extends AccumulatorV2[String, String] {
  //result： 记录的是当前累加器的最终结果
  //session_count=0|1s_3s=0|4s_6s...|60=0

  //初始化常量
  var result = Constants.AGGR_RESULT.toString

  /**
    * 当AccumulatorV2中存在类似数据不存在这种问题时，是否结束程序
    *
    * @return
    */
  override def isZero: Boolean = true

  /**
    * 拷贝一个新的AccumulatorV2(累加器实例)
    *
    * @return
    */
  override def copy(): AccumulatorV2[String, String] = {
    val myAccumulator = new SessionAggrStatAccumulator()//new一个当前累加器
    myAccumulator.result = this.result
    myAccumulator//返回值
  }

  /**
    * 重置AccumulatorV2中的数据
    */
  override def reset(): Unit = {
    result = Constants.AGGR_RESULT.toString
  }

  /**
    * 传入的是key,相同的key就累加1
    * add: 操作数据累加方法实现，session_count=0|1s_3s=2|4s_6s...|60=0
    *
    * @param v 如：session_count
    */
  override def add(v: String): Unit = {
    val v1 = result//记录目前累加器中的一个结果
    val v2 = v
    if (StringUtils.isNotEmpty(v1) && StringUtils.isNotEmpty(v2)) {
      var newResult = ""// 从v1中，提取v2对应的值，并累加
      //0
      //getFieldFromConcatString(v1, "\\|", v2),从旧的字符串(取出v1),用指定"|"分割,求出指定的key的值v2
      val oldValue = StringUtils.getFieldFromConcatString(v1, "\\|", v2)

      if (oldValue != null) {
        val newValue = oldValue.toInt + 1
        //session_count=1|1s_3s=0|4s_6s...|60=0
        //setFieldInConcatString(v1, "\\|", v2, String.valueOf(newValue)),
        // --->String.valueOf(newValue)新的值替换v2,key相同就进行累加
        newResult = StringUtils.setFieldInConcatString(v1, "\\|", v2, String.valueOf(newValue))
      }
      result = newResult
    }
  }

  /**
    * 合并数据
    *
    * @param other
    */
  override def merge(other: AccumulatorV2[String, String]): Unit = other match {
    case map: SessionAggrStatAccumulator =>
      result = other.value
    case _ =>
      throw new UnsupportedOperationException(
        s"Cannot merge ${this.getClass.getName} with ${other.getClass.getName}")
  }

  /**
    * AccumulatorV2对外访问的数据结果
    *
    * @return
    */
  override def value: String = result
}
