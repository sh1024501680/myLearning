package com.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object WordCount {
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

    def wordCount1(sc:SparkContext):Unit={
      val rdd = sc.makeRDD(List("hello scala", "hello spark"))
      val words = rdd.flatMap(_.split(" "))
      val group :RDD[(String,Iterable[String])] = words.groupBy(word => word)
      val wordCount1 = group.mapValues(iter => iter.size)

    }
    def wordCount2(sc:SparkContext):Unit={
      val rdd = sc.makeRDD(List("hello scala", "hello spark"))
      val word = rdd.flatMap(_.split(" ")).map((_,1))
      val wordCount2 :RDD[(String,Int)] = word.reduceByKey(_+_)
    }

    def wordCount3(sc:SparkContext):Unit={
      val rdd = sc.makeRDD(List("hello scala", "hello spark"))
      val word = rdd.flatMap(_.split(" ")).map((_,1))
      val wordCount3 :RDD[(String,Int)] = word.aggregateByKey(0)(_+_,_+_)

    }

    def wordCount4(sc:SparkContext):Unit={
      val rdd = sc.makeRDD(List("hello scala", "hello spark"))
      val word = rdd.flatMap(_.split(" ")).map((_,1))
      val wordCount4 :RDD[(String,Int)] = word.foldByKey(0)(_+_)

    }

    def wordCount5(sc:SparkContext):Unit={
      val rdd = sc.makeRDD(List("hello scala", "hello spark"))
      val word = rdd.flatMap(_.split(" ")).map((_,1))
      val wordCount5 :RDD[(String,Int)] = word.combineByKey(v=>v,(x:Int,y)=>x+y,(x:Int,y:Int)=>x+y)

    }

    def wordCount6(sc:SparkContext):Unit={
      val rdd = sc.makeRDD(List("hello scala", "hello spark"))
      val word = rdd.flatMap(_.split(" ")).map((_,1))
      val wordCount6 :collection.Map[String,Long] = word.countByKey()

    }

    def wordCount7(sc:SparkContext):Unit={
      val rdd = sc.makeRDD(List("hello scala", "hello spark"))
      val word = rdd.flatMap(_.split(" "))
      val wordCount7 :collection.Map[String,Long] = word.countByValue()

    }
    def wordCount8(sc:SparkContext):Unit={
      val rdd = sc.makeRDD(List("hello scala", "hello spark"))
      val word = rdd.flatMap(_.split(" ")).map(
          word=>{
            mutable.Map[String,Long]((word,1))
          })
      val wordCount8 = word.reduce(
        (map1,map2)=> {
          map2.foreach {
            case (word, count) => {
              val newCount = map1.getOrElse(word, 0L) + count
              map1.update(word, newCount)
            }
          }
          map1
        }
      )
    }
  }
}
