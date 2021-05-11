package com.bigdata.spark.core.framework.application

import com.bigdata.spark.core.framework.common.TApplication
import com.bigdata.spark.core.framework.controller.WordCountController

object WordCountApplication extends App with TApplication{

  start(){
    val controller = new WordCountController()
    controller.dispatch()
  }

}
