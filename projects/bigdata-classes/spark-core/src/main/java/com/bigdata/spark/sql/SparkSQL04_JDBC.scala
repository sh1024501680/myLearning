
package com.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql._

object SparkSQL04_JDBC {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().config("spark.master","local[*]").appName("SparkSQL").getOrCreate()

    import spark.implicits._

    val df: DataFrame = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/spark-sql")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "123456")
      .option("dbtable", "user")
      .load()
    df.show()
    df.write
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/spark-sql")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "123456")
      .option("dbtable", "user1")
      .mode(SaveMode.Append)
      .save()

    spark.close()
  }
}