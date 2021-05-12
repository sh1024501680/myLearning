package com.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}

object SparkStreaming08_State_Close {
  def main(args: Array[String]): Unit = {
    /**
     * 线程的关闭：
     *    val thread = new Thread()
     *    thread.start()
     *    thread.stop()  //强制关闭
     */
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingWC")
    val ssc = new StreamingContext(conf, Seconds(3))

    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)
    val wordToOne: DStream[(String, Int)] = lines.map((_, 1))

    wordToOne.print()

    ssc.start()

    /**
     * 如果想要关闭采集器，需要创建新的线程
     * 而且需要在第三方程序中增加关闭状态
     */
    new Thread(new Runnable {
      override def run(): Unit = {
        /**
         * 优雅的关闭
         * 计算节点不再接收新的数据，而是将现有数据处理完毕，然后关闭
         * Mysql:Table(stopSpark) => Row => data 表有数据关闭
         * Redis:Data(K-V)
         * ZK   :/stopSpark
         * hdfs :/stopSpark
         */
        /*while (true){
          if (true){
            //获取SparkStreaming状态
            val state: StreamingContextState = ssc.getState()
            if(state == StreamingContextState.ACTIVE){
              ssc.stop(true,true)
            }
            System.exit(0)
          }
          Thread.sleep(5000)
        }*/
        Thread.sleep(10000)
        val state: StreamingContextState = ssc.getState()
        if(state == StreamingContextState.ACTIVE){
          ssc.stop(true,true)
        }
        System.exit(0)
      }
    }).start()

    ssc.awaitTermination()


  }

}
