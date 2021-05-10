
package spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql._

object SparkSQL05_Hive {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().enableHiveSupport().config("spark.master","local[*]").appName("SparkSQL").getOrCreate()

    spark.sql("show databases").show()


    spark.close()
  }
}