package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark02_RDD_File_parallelize {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("RDD")
    val sc = new SparkContext(conf)

    //文件中的数据作为数据源,默认也可以是设置分区
    //    minPartitions：最小分区数量
    //    math.min(defaultParallelism,2)

    val rdd = sc.textFile("datas/1.txt",3)

    rdd.saveAsTextFile("output")

    sc.stop()

  }
}
