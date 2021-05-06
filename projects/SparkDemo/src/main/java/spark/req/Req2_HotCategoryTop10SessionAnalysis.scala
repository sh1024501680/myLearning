package spark.req

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Req2_HotCategoryTop10SessionAnalysis {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("dep").setMaster("local[*]")
    val sc: SparkContext = new SparkContext(conf)

    val actionRDD: RDD[String] = sc.textFile("datas/")
    actionRDD.cache()

    val top10: Array[String] = top10Category(actionRDD)

    //1.过滤原始数据，保留点击和前10品类ID

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
