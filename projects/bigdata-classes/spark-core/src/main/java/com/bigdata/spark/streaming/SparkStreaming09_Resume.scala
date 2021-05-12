package com.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}

object SparkStreaming09_Resume {
  def main(args: Array[String]): Unit = {

    val ssc: StreamingContext = StreamingContext.getActiveOrCreate("cp", () => {
      val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingWC")
      val ssc = new StreamingContext(conf, Seconds(3))

      val lines: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)
      val wordToOne: DStream[(String, Int)] = lines.map((_, 1))

      wordToOne.print()
      ssc
    })

    ssc.checkpoint("cp")

    ssc.start()

    ssc.awaitTermination()


  }

}
