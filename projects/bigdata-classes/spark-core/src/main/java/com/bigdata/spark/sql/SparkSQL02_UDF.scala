package com.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object SparkSQL02_UDF {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().config("spark.master","local[*]").appName("SparkSQL").getOrCreate()

    import spark.implicits._

    val df: DataFrame = spark.read.json("datas/users.json")
    df.createOrReplaceTempView("user")

    spark.udf.register("prefixName", (name: String) => "Name:" + name)

    spark.sql("select age,prefixName(username) from user").show()

    spark.close()
  }
}