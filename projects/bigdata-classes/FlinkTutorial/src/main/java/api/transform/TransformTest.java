package api.transform;

import bean.SensorReading;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.util.Collector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class TransformTest {
    private StreamExecutionEnvironment env;
    private DataStream dataStream;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(4);
        String inPath = "src\\main\\resources\\sensor.csv";
        dataStream = env.readTextFile(inPath);
    }

    @Test
    public void transformTest1_Base() {
        //1.map 把String转换成字符串长度
        dataStream = dataStream.map((MapFunction<String, Integer>) s -> s.length());
        //2.flatMap 按逗号分隔
        /*dataStream.flatMap((FlatMapFunction<String, String>) (line,out) ->{
            String[] fields = line.split(",");
            for (String field : fields) {
                out.collect(field);
            }
        }).returns(String.class).print("flatMap-lambda");*/
        /*inputStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] fields = s.split(",");
                for (String field : fields) {
                    collector.collect(field);
                }
            }
        });*/
        //3.filter 筛选sensor_1开头的数据
        dataStream.filter((FilterFunction<String>) id -> id.startsWith("sensor_1")).print("filter");

    }

    @Test
    public void transformTest2_RollingAggregation() {
        dataStream = dataStream.map(
                (MapFunction<String,SensorReading>) f-> new SensorReading(
                        f.split(",")[0],
                        Long.parseLong(f.split(",")[1]),
                        Double.parseDouble(f.split(",")[2])
                )
        );
        //分组
        KeyedStream<SensorReading,Tuple2> keyedStream = dataStream.keyBy("id");
        SingleOutputStreamOperator resultStream = keyedStream.maxBy("temperature");
        resultStream.print();
    }

    @Test
    public void transformTest3_Reduce() {
        dataStream = dataStream.map(
                (MapFunction<String,SensorReading>) f-> new SensorReading(
                        f.split(",")[0],
                        Long.parseLong(f.split(",")[1]),
                        Double.parseDouble(f.split(",")[2])
                )
        );
        KeyedStream<SensorReading,String> keyedStream = dataStream.keyBy("id");
        keyedStream.reduce(
                (curSensor, newData) -> new SensorReading(
                                curSensor.getId(),
                                newData.getTimeStamp(),
                                Math.max(curSensor.getTemperature(), newData.getTemperature())
                        )).print();
    }

    /*@Test
    public void transformTest4_MultipleStreams() {
        dataStream = dataStream.map(
                (MapFunction<String,SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])
        ));
        //1 分流 (split过时，可用侧输出流替代)
        SplitStream splitStream = dataStream.split((OutputSelector<SensorReading>) o -> Collections.singleton(o.getTemperature() > 30 ? "high" : "low"));
        DataStream<SensorReading> high = splitStream.select("high");
        DataStream<SensorReading> low = splitStream.select("low");
        DataStream<SensorReading> all = splitStream.select("high", "low");
        //2 合流  将高温流转换成二元组类型，与低温流连接合并之后，输出状态信息
        DataStream<Tuple2<String,Double>> highStream = high.map(new MapFunction<SensorReading, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(SensorReading sensorReading) throws Exception {
                return new Tuple2<>(sensorReading.getId(),sensorReading.getTemperature());
            }
        });
        ConnectedStreams<Tuple2<String, Double>, SensorReading> warningStream = highStream.connect(low);
        warningStream.map(new CoMapFunction<Tuple2<String, Double>, SensorReading, Object>() {
            @Override
            public Object map1(Tuple2<String, Double> stringDoubleTuple2) throws Exception {
                return new Tuple3<>(stringDoubleTuple2.f0, stringDoubleTuple2.f1, "high temp warning");
            }

            @Override
            public Object map2(SensorReading sensorReading) throws Exception {
                return new Tuple2<>(sensorReading, "normal");
            }
        }).print();
        // union 合流(要求数据类型一致)
        high.union(low);
    }*/

    @Test
    public void TransformTest5_RichFunction() {
        dataStream = dataStream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])
        ));

        DataStream<Tuple2<String, Integer>> resStream = dataStream.map(new RichMapFunction<SensorReading,Tuple2<String,Integer>>() {
            @Override
            public void open(Configuration parameters) throws Exception {
                // 初始化，一般是定义状态，或者建立数据库连接
                System.out.println("open");
            }

            @Override
            public Tuple2<String, Integer> map(SensorReading sensorReading) throws Exception {
                getRuntimeContext();
                return new Tuple2<>(sensorReading.getId(),getRuntimeContext().getIndexOfThisSubtask());
            }

            @Override
            public void close() {
                System.out.println("关闭连接、清理状态");
            }
        });
    }

    @Test
    public void TransformTest6_Partition() {
        dataStream.print("input");
        // shuffle
        DataStream<String> shuffleStream = dataStream.shuffle();
        shuffleStream.print("shuffle");
        dataStream = dataStream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));
        // keyBy
        dataStream.keyBy("id").print("key");
        // global
        dataStream.global().print("global");
    }

    @After
    public void exec() throws Exception {
        env.execute("transform test");
    }
}
