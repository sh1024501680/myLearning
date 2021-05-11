package com.bigdata.spark.core.framework.common

import spark.core.framework.util.EnvUtil

trait TDao {

  def readFile(path:String) ={
    EnvUtil.take().textFile(path)
  }
}
