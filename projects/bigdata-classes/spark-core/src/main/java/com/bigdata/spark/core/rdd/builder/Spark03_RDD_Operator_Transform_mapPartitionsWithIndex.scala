package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark03_RDD_Operator_Transform_mapPartitionsWithIndex {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("RDD")
    val sc = new SparkContext(conf)

    val rdd = sc.makeRDD(List(1,2,3,4),2)
    //[1,2],[3,4]
    val mapPIrdd = rdd.mapPartitionsWithIndex((index, iter) => {
      if (index == 1) {
        iter
      } else
        Nil.iterator
    })

    mapPIrdd.collect().foreach(println(_))

    sc.stop()

  }
}
