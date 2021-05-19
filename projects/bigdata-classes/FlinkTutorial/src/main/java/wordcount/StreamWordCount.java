package wordcount;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class StreamWordCount {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        创建远程执行环境，参数：JobManager的主机名，rpc通信端口号，jar路径
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("jobmanager-host", 6123, "/tmp/xx.jar");

//        env.setParallelism(1);
//        env.disableOperatorChaining();

        /*String inPath = "datas/words.txt";
        DataStream<String> stream = env.readTextFile(inPath);*/

        /*DataStream<String> stream = env.socketTextStream("localhost", 7777);*/
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String host = parameterTool.get("host");
        Integer port = parameterTool.getInt("port");
        DataStream<String> stream = env.socketTextStream(host, port);

        DataStream<Tuple2<String, Integer>> resultStream = stream
                .flatMap(new WordCount.MyFlatMapFunction())
//                .slotSharingGroup("")  设置slot共享组(默认跟前一步操作为同一共享组)
//                .startNewChain()  开始一个新的任务链
//                .disableChaining()  当前任务链不合并
                .keyBy(0)
                .sum(1);

        resultStream.print();

        env.execute("stream word count test");

    }
}
