package com.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object SparkStreaming02_Queue {
  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingWC")
    val ssc = new StreamingContext(conf, Seconds(3))

    val rddQueue = new mutable.Queue[RDD[Int]]()

    val inputStream: InputDStream[Int] = ssc.queueStream(rddQueue,oneAtATime = false)

    val mapStream: DStream[(Int, Int)] = inputStream.map((_, 1))
    val reduceStream: DStream[(Int, Int)] = mapStream.reduceByKey(_ + _)

    reduceStream.print()

    //1.启动采集器
    ssc.start()

    for (i <- 1 to 5){
      rddQueue += ssc.sparkContext.makeRDD(1 to 300,10)
      Thread.sleep(2000)
    }

    //2.等待采集器关闭
    ssc.awaitTermination()
  }
}
