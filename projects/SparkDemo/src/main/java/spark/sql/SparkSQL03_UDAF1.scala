package spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql._

object SparkSQL03_UDAF1 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark: SparkSession = SparkSession.builder().config("spark.master","local[*]").appName("SparkSQL").getOrCreate()

    val df: DataFrame = spark.read.json("datas/users.json")
    df.createOrReplaceTempView("user")

    spark.udf.register("ageAVG", functions.udaf(new MyAvgUDAF()))

    spark.sql("select ageAVG(age) from user").show()

    spark.close()
  }
  /*
  * 自定义聚合函数类
  * 1.继承 org.apache.spark.sql.expressions.Aggregator，定义泛型
  *   IN：输入的数据类型 Long
  *   BUF：缓冲区的类型：Buff
  *   OUT：输出的数据类型 Long
  * 2.重写方法
  */
  case class Buff(var total:Long,var count:Long)
  class MyAvgUDAF extends Aggregator[Long,Buff,Long]{
    //z | zero:初始值或零值
    // 缓冲区的初始化
    override def zero: Buff = {
      Buff(0L,0L)
    }

    //根据输入的数据更新缓冲区的数据
    override def reduce(b: Buff, a: Long): Buff = {
      b.total = b.total + a
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