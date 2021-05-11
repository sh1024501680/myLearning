package com.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object Spark06_RDD_Operator_Transform_aggtrgateByKey {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(conf)

    val rdd = sc.makeRDD(List(("a",1),("a",2),("b",3),("b",4),("b",5),("a",6)),2)
    //aggregateByKey 存在函数柯里化，两个参数列表
    //第一个参数列表,需要传递一个参数,表示为初始值
    //第二个参数列表需要传递2个参数
    //    第一个参数表示分区内计算规则
    //    第二个参数表示分区间计算规则
    rdd.aggregateByKey(0)((x,y)=>math.max(x,y),(x,y)=>x+y).collect().foreach(println)

    sc.stop()

  }
}
