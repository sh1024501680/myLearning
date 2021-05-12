package com.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming01_WordCount {
  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingWC")
    val ssc = new StreamingContext(conf, Seconds(3))

    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)

    val words: DStream[String] = lines.flatMap(_.split(" "))

    val word: DStream[(String, Int)] = words.map((_, 1))

    val wc: DStream[(String, Int)] = word.reduceByKey(_ + _)

    wc.print()

    //采集器长期执行，不能直接关闭
    //main执行完毕，应用程序自动结束，所以不能让main执行完毕
    //ssc.stop()
    //1.启动采集器
    ssc.start()
    //2.等待采集器关闭
    ssc.awaitTermination()
  }
}
