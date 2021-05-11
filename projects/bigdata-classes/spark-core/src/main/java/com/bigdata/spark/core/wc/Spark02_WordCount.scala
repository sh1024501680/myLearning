package com.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark02_WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)

    val lines:RDD[String] = sc.textFile("datas")

    val words = lines.flatMap(_.split(" "))

    val wordToOne = words.map((_, 1))

    val wordGroup = wordToOne.groupBy(words=>(words._1))

    val wordToCount = wordGroup.map{
      case (word,list)=>{
        list.reduce(
          (t1,t2)=>(t1._1,t1._2+t2._2)
        )
      }
    }

    val array = wordToCount.collect()

    array.foreach(println(_))

    sc.stop()
  }
}
