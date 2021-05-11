package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Memory_parallelize {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(conf)

    // 内存中创建RDD

    //makeRDD 第二个参数表示分区数量 ，不传会有默认值：defaultParallelism
    //分区1 [1,2],分区2 [3,4]
    //val rdd = sc.makeRDD(List(1,2,3,4),2)
    //分区1 [1],分区2 [2,3], 分区3 [4,5]
    //val rdd = sc.makeRDD(List(1,2,3,4,5),3)
    val rdd = sc.makeRDD(List(1,2,3,4,5),3)

    rdd.saveAsTextFile("output")

    sc.stop()

  }
}
