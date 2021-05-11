package com.bigdata.spark.core.rdd.builder

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object Spark05_RDD_Operator_Transform_simple {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(conf)

    val rdd = sc.makeRDD(List(1,2,3,4,5,6,7,8,9,10))

    //sample需要3个参数
    //1.第一个参数表示  抽取后是否将数据返回true(放回)，false(丢弃)
    //2.第二个参数表示
    //      抽取不放回：数据源中每条数据被抽取的概率
    //      抽取放回：数据源中每条数据被抽取的次数
    //3.第三个参数   随机数种子(不传递使用当前系统时间)
    println(rdd.sample(true, 0.4).collect().mkString(","))

    sc.stop()

  }
}
