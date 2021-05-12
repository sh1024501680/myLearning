package com.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming05_State {
  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingWC")
    val ssc = new StreamingContext(conf, Seconds(3))

    ssc.checkpoint("datas/cp")

    //无状态数据操作，只对当前采集周期内数据进行处理
    // 某些场合下需要保留数据统计结果（状态），实现数据汇总
    // 使用有状态操作时需要设置检查点路径
    val datas: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)

    val wordCount = datas.map((_, 1))

    // updateStateByKey 根据key对数据状态进行更新
    // 传递的参数中含有两个值
    // 第一个值表示相同的key的value数据
    // 第二个表示缓冲区相同key的value数据
    val state: DStream[(String, Int)] = wordCount.updateStateByKey((seq: Seq[Int], buff: Option[Int]) => {
      val newCnt = buff.getOrElse(0) + seq.sum
      Option(newCnt)
    })

    state.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
