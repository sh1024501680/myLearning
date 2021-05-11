package com.bigdata.spark.core.operator.transform

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Operator_Transform {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("operator")
    val sc = new SparkContext(conf)

    val rdd = sc.makeRDD(List(1,2,3,4))

    //转换函数
    def mapFunction(num:Int):Int = {
      num * 2
    }

    //rdd.map(mapFunction)
    val mapRDD = rdd.map(_ * 2)

    mapRDD.collect().foreach(println(_))

  }
}
