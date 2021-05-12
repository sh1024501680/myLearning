package com.bigdata.spark.streaming

import java.util.Random

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming03_DIY {
  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingWC")
    val ssc = new StreamingContext(conf, Seconds(3))

    val msgDs: ReceiverInputDStream[String] = ssc.receiverStream(new MyReceiver())
    msgDs.print()

    ssc.start()
    ssc.awaitTermination()
  }

  /**
   * 1.继承Receiver，定义泛型，传递参数
   * 2.重写方法
   */
  class MyReceiver extends Receiver[String](StorageLevel.MEMORY_ONLY){
    private var flag = true
    override def onStart(): Unit = {
      new Thread(new Runnable {
        override def run(): Unit = {
          while (flag){
            val message = "采集的数据为："+ new Random().nextInt(10).toString
            store(message)
            Thread.sleep(500)
          }
        }
      }).start()
    }

    override def onStop(): Unit = {
      flag = false
    }
  }
}
