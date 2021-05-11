package com.bigdata.spark.core.framework.service

import org.apache.spark.rdd.RDD
import com.bigdata.spark.core.framework.common.TService
import com.bigdata.spark.core.framework.dao.WordCountDao

class WordCountService extends TService{

  private val wordCountDao = new WordCountDao()

  def dataAnalysis():Array[(String, Int)]={

    val lines: RDD[String] = wordCountDao.readFile("datas/words.txt")
    val words: RDD[String] = lines.flatMap(_.split(" "))
    val resRDD: RDD[(String, Int)] = words.map((_, 1)).reduceByKey(_ + _)
    val array: Array[(String, Int)] = resRDD.collect()
    array
  }
}
