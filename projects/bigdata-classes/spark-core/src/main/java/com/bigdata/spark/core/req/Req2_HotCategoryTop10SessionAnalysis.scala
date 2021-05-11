package com.bigdata.spark.core.req

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Req2_HotCategoryTop10SessionAnalysis {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("Req2_Top10SessionAnalysis").setMaster("local[*]")
    val sc: SparkContext = new SparkContext(conf)

    val actionRDD: RDD[String] = sc.textFile("datas/user_visit_action.txt")
    actionRDD.cache()

    val top10: Array[String] = top10Category(actionRDD)

    //1.过滤原始数据，保留点击和前10品类ID
    val filterRDD: RDD[String] = actionRDD.filter(
      action => {
        val datas: Array[String] = action.split("_")
        if (datas(6) != "-1") {
          top10.contains(datas(6))
        } else {
          false
        }
      }
    )

    val reduceRDD: RDD[((String, String), Int)] = filterRDD.map(
      action => {
        val datas: Array[String] = action.split("_")
        ((datas(6), datas(2)), 1)
      }
    ).reduceByKey(_+_)

    val mapRDD: RDD[(String, (String, Int))] = reduceRDD.map {
      case ((cid, sid), sum) => {
        (cid, (sid, sum))
      }
    }

    // 4.将相同品类进行分组
    val groupRDD: RDD[(String, Iterable[(String, Int)])] = mapRDD.groupByKey()

    // 5.将分组后的数据进行点击量的排序，取前10名
    val resultRDD: RDD[(String, List[(String, Int)])] = groupRDD.mapValues(
      iter => {
        iter.toList.sortBy(_._2)(Ordering.Int.reverse).take(10)
      }
    )

    resultRDD.collect().foreach(println)

    sc.stop()
  }
  def top10Category(actionRDD: RDD[String])={
    val flatRDD: RDD[(String, (Int, Int, Int))] = actionRDD.flatMap(
      action => {
        val datas: Array[String] = action.split("_")
        if (datas(6) != "-1") {
          //点击
          List((datas(6), (1, 0, 0)))
        } else if (datas(8) != "null") {
          //下单
          val ids: Array[String] = datas(8).split(",")
          ids.map(id => (id, (0, 1, 0)))
        } else if (datas(10) != "null") {
          //支付
          val ids: Array[String] = datas(10).split(",")
          ids.map(id => (id, (0, 0, 1)))
        } else {
          Nil
        }
      }
    )
    val analysisRDD: RDD[(String, (Int, Int, Int))] = flatRDD.reduceByKey(
      (t1, t2) => (t1._1 + t2._1, t1._2 + t2._2, t1._3 + t2._3)
    )
    val resultRDD: Array[(String, (Int, Int, Int))] = analysisRDD.sortBy(_._2, false).take(10)

    resultRDD.map(_._1)
  }
}
