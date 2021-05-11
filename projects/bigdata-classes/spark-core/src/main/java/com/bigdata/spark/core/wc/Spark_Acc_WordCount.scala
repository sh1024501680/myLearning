package com.bigdata.spark.core.wc

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

object Spark_Acc_WordCount {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local").setAppName("Acc")
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[String] = sc.makeRDD(List("hello","spark","hello"))

    //创建累加器对象
    val wcAcc = new MyAccumulator()
    //向Spark注册
    sc.register(wcAcc)
    rdd.foreach(
      word=>{
        wcAcc.add(word)
      }
    )
    println(wcAcc)
  }

  class MyAccumulator extends AccumulatorV2[String,mutable.Map[String,Long]]{

    private var wcMap = mutable.Map[String,Long]()

    //判断是否为初始状态
    override def isZero: Boolean = wcMap.isEmpty

    override def copy(): AccumulatorV2[String, mutable.Map[String, Long]] = new MyAccumulator()

    override def reset(): Unit = wcMap.clear()

    //获取累加器需要计算的值
    override def add(word: String): Unit = {
      val newCnt = wcMap.getOrElse(word,0L) + 1
      wcMap.update(word,newCnt)
    }

    override def merge(other: AccumulatorV2[String, mutable.Map[String, Long]]): Unit = {
      val map1 = this.wcMap
      val map2 = other.value

      map2.foreach(
        {
          case (word,count) => {
            val newCount = map1.getOrElse(word, 0L) + count
            map1.update(word,newCount)
          }
        }
      )
    }

    override def value: mutable.Map[String, Long] = wcMap
  }
}
