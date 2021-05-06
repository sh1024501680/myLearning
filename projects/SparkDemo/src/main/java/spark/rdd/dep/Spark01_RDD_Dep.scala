package spark.rdd.dep

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Dep {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("dep").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val line: RDD[String] = sc.textFile("datas/words.txt")

    println(line.dependencies)
    println("**********************")

    val words: RDD[String] = line.flatMap(_.split(" "))
    println(words.dependencies)
    println("**********************")

    val mapRDD: RDD[(String, Int)] = words.map((_, 1))
    println(mapRDD.dependencies)
    println("**********************")

    val wordCountRDD: RDD[(String, Int)] = mapRDD.reduceByKey(_ + _)
    println(wordCountRDD.dependencies)
  }
}
