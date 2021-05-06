package spark.core.framework.controller

import spark.core.framework.common.TController
import spark.core.framework.service.WordCountService

class WordCountController extends TController{

  private val wordCountService = new WordCountService()

  def dispatch(): Unit ={
    val array: Array[(String, Int)] = wordCountService.dataAnalysis()
    array.foreach(println)
  }
}
