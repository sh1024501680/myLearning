package com.bigdata.spark.core.rdd.builder

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark04_RDD_Operator_Transform_glom {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(conf)

    val rdd = sc.makeRDD(List(1,2,3,4),2)

    val glomRDD: RDD[Array[Int]] = rdd.glom()

    val maxRDD = glomRDD.map(arr => arr.max)

    println(maxRDD.collect().sum)

    sc.stop()

  }
}
