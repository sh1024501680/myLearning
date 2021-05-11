package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark03_RDD_Req {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("RDD")
    val sc = new SparkContext(conf)

    val rdd = sc.textFile("hdfs://mycluster/datas")

    val reduceRDD = rdd.map(
      line => {
        val data = line.split(" ")
        ((data(1), data(4)), 1)
      }).reduceByKey(_+_)

    val mapRDD = reduceRDD.map {
      case ((province, ad), sum) => {
        (province, (ad, sum))
      }
    }

    val groupRDD = mapRDD.groupByKey()

    val res = groupRDD.mapValues(
      iter => {
        iter.toList.sortBy(_._2)(Ordering.Int.reverse).take(3)
      }
    )
    
    res.collect().foreach(println)

    sc.stop()

  }
}
