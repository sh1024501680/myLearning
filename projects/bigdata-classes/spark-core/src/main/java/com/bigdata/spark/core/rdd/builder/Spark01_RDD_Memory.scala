package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Memory {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("RDD")
    val sc = new SparkContext(conf)

    // 内存中创建RDD
    val seq = Seq[Int](1,2,3,4)

    //parallelize:并行
    val rdd1 = sc.parallelize(seq)

    val rdd2 = sc.makeRDD(seq)


    rdd2.collect().foreach(println)

    sc.stop()

  }
}
