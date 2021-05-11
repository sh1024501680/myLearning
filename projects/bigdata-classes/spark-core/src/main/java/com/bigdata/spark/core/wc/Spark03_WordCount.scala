package com.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark03_WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    val lines:RDD[String] = sc.textFile("datas")

    val words = lines.flatMap(_.split(" "))

    val wordToOne = words.map((_, 1))

    val wordToCount = wordToOne.reduceByKey(_+_)

    val array = wordToCount.collect()

    array.foreach(println(_))

    sc.stop()
  }
}
