
package com.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object SparkSQL01 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    //val spark: SparkSession = SparkSession.builder().config("spark.master","local[*]").appName("SparkSQL").getOrCreate()
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    val df: DataFrame = spark.read.json("datas/users.json")
    df.write.json("output")
    //    df.show()
    //    df.createOrReplaceTempView("user")
    /*spark.sql("select * from user").show()
    spark.sql("select age from user").show()
    spark.sql("select avg(age) from user").show()*/

    /*df.select("username","age").show()
    df.select($"age" + 1)
    df.select('age + 1)*/

    val rdd: RDD[(Int, String, Int)] = spark.sparkContext.makeRDD(List((1, "zhangsan", 30), (2, "lisi", 40)))
//    val df: DataFrame = rdd.toDF("id", "name", "age")
//    val rowRDD: RDD[Row] = df.rdd

    val ds: Dataset[User] = df.as[User]
    val df1: DataFrame = ds.toDF()

    val ds1: Dataset[User] = rdd.map {
      case (id, name, age) => {
        User(id, name, age)
      }
    }.toDS()
    val dsToRDD: RDD[User] = ds1.rdd


    spark.close()
  }
  case class User(id:Int,name:String,age:Int)
}
