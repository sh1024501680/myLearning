
package com.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.{Aggregator, MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, Row, SparkSession, TypedColumn, functions}

object SparkSQL03_UDAF2 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().config("spark.master","local[*]").appName("SparkSQL").getOrCreate()

    import spark.implicits._

    val df: DataFrame = spark.read.json("datas/users.json")
    val ds: Dataset[User] = df.as[User]

    //DSL 使用 UDAF 查询
    val udafCol: TypedColumn[User, Long] = new MyAvgUDAF().toColumn

    ds.select(udafCol).show()


    spark.close()
  }
  /*
  * 自定义聚合函数类
  * 1.继承 org.apache.spark.sql.expressions.Aggregator，定义泛型
  *   IN：输入的数据类型 User
  *   BUF：缓冲区的类型：Buff
  *   OUT：输出的数据类型 Long
  * 2.重写方法
  */
  case class User(username:String,age:Long)
  case class Buff(var total:Long,var count:Long)
  class MyAvgUDAF extends Aggregator[User,Buff,Long]{
    //z | zero:初始值或零值
    // 缓冲区的初始化
    override def zero: Buff = {
      Buff(0L,0L)
    }

    //根据输入的数据更新缓冲区的数据
    override def reduce(b: Buff, a: User): Buff = {
      b.total = b.total + a.age
      b.count = b.count + 1
      b
    }

    //合并缓冲区
    override def merge(b1: Buff, b2: Buff): Buff = {
      b1.total = b1.total + b2.total
      b1.count = b1.count + b2.count
      b1
    }

    //计算结果
    override def finish(reduction: Buff): Long = {
      reduction.total / reduction.count
    }

    //缓冲区的编码操作
    override def bufferEncoder: Encoder[Buff] = Encoders.product

    //输出的编码操作
    override def outputEncoder: Encoder[Long] = Encoders.scalaLong
  }
}