package com.bigdata.spark.core.framework.common

import org.apache.spark.{SparkConf, SparkContext}
import spark.core.framework.util.EnvUtil

trait TApplication {

  def start(master:String="local[*]",app:String="Application")( op: =>Unit): Unit ={
    val conf: SparkConf = new SparkConf().setMaster(master).setAppName(app)
    val sc: SparkContext = new SparkContext(conf)
    EnvUtil.put(sc)

    try {
       op
    }catch {
      case ex=> println(ex.getMessage)
    }

    EnvUtil.clear()
  }
}
