package api.source;

import bean.SensorReading;
import org.apache.commons.collections.IteratorUtils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WindowTest {
    private StreamExecutionEnvironment env;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
    }

    @Test
    public void windowTest1_TimeWindow() {
        env.setParallelism(1);
        DataStreamSource<String> stream = env.socketTextStream("localhost", 7777);
        DataStream<SensorReading> dataStream = stream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));
        //开窗测试
        dataStream.keyBy("id")
                .timeWindow(Time.seconds(15))
//                .window(TumblingProcessingTimeWindows.of(Time.seconds(15)));
//                .window(EventTimeSessionWindows.withGap(Time.seconds(30)));
//                .countWindow(10, 2)
        //1.增量聚合
                .aggregate(new AggregateFunction<SensorReading, Integer, Integer>() {
                    @Override
                    public Integer createAccumulator() {
                        return 0;
                    }

                    @Override
                    public Integer add(SensorReading sensorReading, Integer integer) {
                        return integer + 1 ;
                    }

                    @Override
                    public Integer getResult(Integer integer) {
                        return integer;
                    }

                    @Override
                    public Integer merge(Integer integer, Integer acc1) {
                        return integer + acc1;
                    }
                })
                //.print()
        ;
        //2.全窗口函数
        SingleOutputStreamOperator<Tuple3<String,Long,Integer>> resultStream = dataStream.keyBy("id")
                .timeWindow(Time.seconds(15))
                .apply(new WindowFunction<SensorReading, Tuple3<String,Long,Integer>, Tuple, TimeWindow>() {
                    @Override
                    public void apply(Tuple tuple, TimeWindow timeWindow, Iterable<SensorReading> iterable, Collector<Tuple3<String,Long,Integer>> collector) throws Exception {
                        String id = tuple.getField(0);
                        Integer count = IteratorUtils.toList(iterable.iterator()).size();
                        collector.collect(new Tuple3<>(id,timeWindow.getEnd(),count));
                    }
                });
        resultStream.print();
        //3.其他可选API
        OutputTag<SensorReading> late = new OutputTag<>("late");
        SingleOutputStreamOperator<SensorReading> sumStream = dataStream.keyBy("id")
                .timeWindow(Time.seconds(15))
//                .trigger()
//                .evictor()
                .allowedLateness(Time.minutes(1))
                .sideOutputLateData(late)
                .sum("temperature");
        sumStream.getSideOutput(late).print("late");
    }

    @Test
    public void windowTest2_CountWindow() {
        DataStreamSource<String> stream = env.socketTextStream("localhost", 7777);
        DataStream<SensorReading> dataStream = stream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));
        SingleOutputStreamOperator<Double> resultStream = dataStream.keyBy("id")
                .countWindow(10, 2)
                .aggregate(new MyAvgTemp());
        resultStream.print();
    }

    @Test
    public void windowTest3_EventTimeWindow() {
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(100);
        DataStreamSource<String> stream = env.socketTextStream("localhost", 7777);
        DataStream<SensorReading> dataStream = stream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])))
                /*  升序数据设置事件时间和watermark
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<SensorReading>() {
                    @Override
                    public long extractAscendingTimestamp(SensorReading element) {
                        return element.getTimeStamp() * 1000L;
                    }
                })*/
                //乱序时间设置时间戳和watermark
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(2)) {
                    @Override
                    public long extractTimestamp(SensorReading element) {
                        return element.getTimeStamp() * 1000L;
                    }
                });

        //侧输出流标签
        OutputTag<SensorReading> late = new OutputTag<>("late");
        //基于事件时间的开窗聚合，统计15s内的温度最小值
        SingleOutputStreamOperator<SensorReading> minTempStream = dataStream.keyBy("id")
                .timeWindow(Time.seconds(15))
                .allowedLateness(Time.minutes(1))
                .minBy("temperature");

        minTempStream.print("minTemp");
        minTempStream.getSideOutput(late).print("late");
    }



    private static class MyAvgTemp implements AggregateFunction<SensorReading, Tuple2<Double,Integer>,Double>{

        @Override
        public Tuple2<Double, Integer> createAccumulator() {
            return new Tuple2<>(0.0,0);
        }
        @Override
        public Tuple2<Double, Integer> add(SensorReading sensorReading, Tuple2<Double, Integer> doubleIntegerTuple2) {
            return new Tuple2<>(sensorReading.getTemperature() + doubleIntegerTuple2.f0, doubleIntegerTuple2.f1 + 1);
        }

        @Override
        public Double getResult(Tuple2<Double, Integer> doubleIntegerTuple2) {
            return doubleIntegerTuple2.f0 / doubleIntegerTuple2.f1;
        }

        @Override
        public Tuple2<Double, Integer> merge(Tuple2<Double, Integer> doubleIntegerTuple2, Tuple2<Double, Integer> acc1) {
            return new Tuple2<>(doubleIntegerTuple2.f0 + acc1.f0, doubleIntegerTuple2.f1 + acc1.f1);
        }

    }

    @After
    public void exec() throws Exception {
        env.execute("window test");
    }
}
