package com.bigdata.spark.core.acc

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_Acc {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local").setAppName("Acc")
    val sc: SparkContext = new SparkContext(conf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))

    val sumAcc: LongAccumulator = sc.longAccumulator("sum")

    rdd.foreach(
      num=>{
        sumAcc.add(num)
      }
    )

    println(sumAcc.value)

    sc.stop()
  }

}
