package com.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming07_State_Output {
  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingWC")
    val ssc = new StreamingContext(conf, Seconds(3))

    ssc.checkpoint("cp")

    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)

    val wordToOne: DStream[(String, Int)] = lines.map((_, 1))

    // reduceByKeyAndWindow：当窗口范围比较大，但滑动幅度比较小，可以采用增加数据和删除数据方式，无需重新计算
    val windowDS: DStream[(String, Int)] = wordToOne.reduceByKeyAndWindow(
      (x:Int,y:Int)=>x+y,
      (x:Int,y:Int)=>x-y,
      Seconds(9),Seconds(3)
    )

    windowDS.foreachRDD(
      rdd=>{
        rdd.foreach(println)
      }
    )
    //windowDS.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
