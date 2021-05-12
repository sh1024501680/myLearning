package com.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming06_Join {
  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingWC")
    val ssc = new StreamingContext(conf, Seconds(3))

    val data9999: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)
    val data8888: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 8888)

    val map9999: DStream[(String, Int)] = data9999.map((_, 1))
    val map8888: DStream[(String, Int)] = data8888.map((_, 1))

    /**
     * 所谓的join操作，其实就是两个RDD的join
     */
    val joinDS: DStream[(String, (Int, Int))] = map9999.join(map8888)

    joinDS.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
