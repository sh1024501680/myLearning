package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark02_RDD_File1 {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("RDD")
    val sc = new SparkContext(conf)

    //文件中的数据作为数据源
    //textFile:以行为单位来读取数据
    //wholeTextFiles:以文件为单位读取数据
    //   读取的结果为一个元组，第一个元素表示文件路径，第二个元素表示读取的内容
    val rdd = sc.wholeTextFiles("datas")

    rdd.collect().foreach(println)

    sc.stop()

  }
}
