package com.bigdata.spark.core.framework.controller

import com.bigdata.spark.core.framework.common.TController
import com.bigdata.spark.core.framework.service.WordCountService

class WordCountController extends TController{

  private val wordCountService = new WordCountService()

  def dispatch(): Unit ={
    val array: Array[(String, Int)] = wordCountService.dataAnalysis()
    array.foreach(println)
  }
}
