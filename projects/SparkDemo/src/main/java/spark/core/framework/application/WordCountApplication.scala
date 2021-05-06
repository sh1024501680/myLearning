package spark.core.framework.application

import spark.core.framework.common.TApplication
import spark.core.framework.controller.WordCountController

object WordCountApplication extends App with TApplication{

  start(){
    val controller = new WordCountController()
    controller.dispatch()
  }

}
