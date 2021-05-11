package com.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object SparkSQL03_UDAF {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().config("spark.master","local[*]").appName("SparkSQL").getOrCreate()

    val df: DataFrame = spark.read.json("datas/users.json")
    df.createOrReplaceTempView("user")

    spark.udf.register("ageAVG", new MyAvgUDAF())

    spark.sql("select ageAVG(age) from user").show()

    spark.close()
  }
  /*
  * 自定义聚合函数类
  * 1.继承 UserDefinedAggregateFunction
  * 2.重写方法
  */
  class MyAvgUDAF extends UserDefinedAggregateFunction{
    //输入数据的结构：In
    override def inputSchema: StructType = {
      StructType(Array(StructField("age",LongType)))
    }

    //缓冲区数据的结构：Buffer
    override def bufferSchema: StructType = {
      StructType(Array(
        StructField("total",LongType),
        StructField("count",LongType)
      ))
    }

    //函数计算结果的数据类型：Out
    override def dataType: DataType = {
      LongType
    }

    //函数的稳定性：传入相同参数函数结果是否相同
    override def deterministic: Boolean = true

    //缓冲区初始化
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
//      buffer(0) = 0L
//      buffer(1) = 0L

      buffer.update(0,0L)
      buffer.update(1,0L)
    }

    //根据输入的值更新缓冲区数据
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      buffer.update(0,buffer.getLong(0)+input.getLong(0))
      buffer.update(1,buffer.getLong(1)+1)
    }

    //缓冲区数据合并
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      buffer1.update(0,buffer1.getLong(0)+buffer2.getLong(0))
      buffer1.update(1,buffer1.getLong(1)+buffer2.getLong(1))
    }

    //计算平均值
    override def evaluate(buffer: Row): Any = {
      buffer.getLong(0)/buffer.getLong(1)
    }
  }
}