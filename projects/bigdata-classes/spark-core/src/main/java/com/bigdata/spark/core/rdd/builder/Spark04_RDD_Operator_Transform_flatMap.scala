package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark04_RDD_Operator_Transform_flatMap {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(conf)

    val rdd = sc.makeRDD(List(List(1,2),3,List(4,5)))

    val flatRDD = rdd.flatMap(
        data => {
          data match {
            case list:List[_] => list
            case dat => List(dat)
          }
        })

    flatRDD.collect().foreach(println)

    sc.stop()

  }
}
