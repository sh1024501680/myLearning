package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark02_RDD_File {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("RDD")
    val sc = new SparkContext(conf)

    //文件中的数据作为数据源
    //path路径默认以当前环境的根路径为基准。可以写绝对路径，也可以写相对路径（相对当前项目的根路径）
    //val rdd = sc.textFile("datas/1.txt")
    //val rdd = sc.textFile("datas")
    val rdd = sc.textFile("hdfs://mycluster/datas")

    rdd.collect().foreach(println)

    sc.stop()

  }
}
