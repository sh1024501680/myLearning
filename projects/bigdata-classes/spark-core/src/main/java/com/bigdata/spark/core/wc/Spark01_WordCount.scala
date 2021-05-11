package com.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    val lines:RDD[String] = sc.textFile("datas")

    val words = lines.flatMap(_.split(" "))

    val wordGroup = words.groupBy(words=>words)

    val wordToCount = wordGroup.map{
      case (word,list)=>{(word,list.size)}
    }

    val array = wordToCount.collect()

    array.foreach(println(_))

    sc.stop()
  }
}
