package com.bigdata.spark.util

import java.sql.Connection
import java.util.Properties

import javax.sql.DataSource
import com.alibaba.druid.pool.DruidDataSourceFactory

object JDBCUtil {

  //初始化连接池
  var dataSource: DataSource = init()
  //初始化连接池方法
  def init(): DataSource = {
    val properties = new Properties()
    properties.setProperty("driverClassName", "com.mysql.jdbc.Driver")
    properties.setProperty("url", "")
    properties.setProperty("username", "root")
    properties.setProperty("password", "123456")
    properties.setProperty("maxActive", "50")
    DruidDataSourceFactory.createDataSource(properties)
  }
  //获取 MySQL 连接
  def getConnection: Connection = {
    dataSource.getConnection
  }

}
