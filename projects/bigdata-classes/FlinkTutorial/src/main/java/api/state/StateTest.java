package api.state;

import bean.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StateTest {

    private StreamExecutionEnvironment env;
    private DataStream<String> stream;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        String inPath = "D:\\JavaProjects\\IdeaProject\\FlinkTutorial\\src\\main\\resources\\sensor.csv";
        stream = env.readTextFile(inPath);
    }

    @Test
    public void stateTest1_OperatorState() {
        DataStream<SensorReading> dataStream = stream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));

        //定义一个有状态的map操作,统计当前分区数据个数
        dataStream.map(new MyCountMapper()).print();

    }
    @Test
    public void stateTest2_KeyedState() {
        DataStream<SensorReading> dataStream = stream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));

        //定义一个有状态的map操作,统计当前sensor数据个数
        dataStream.keyBy("id")
                .map(new MyKeyCountMapper()).print();

    }

    @Test
    public void stateTest3_TempWarn() {
        DataStream<SensorReading> dataStream = env.socketTextStream("localhost", 7777).map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));
        DataStream<Tuple3<String, Double, Double>> warningStream = dataStream.keyBy("id")
                .flatMap(new TempWarning(10.0));
        warningStream.print();
    }

    @Test
    public void stateTest4_FaultTolerance() throws IOException {
        DataStream<SensorReading> dataStream = env.socketTextStream("localhost", 7777).map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));
        // 状态后端的配置
        env.setStateBackend(new MemoryStateBackend());
        env.setStateBackend(new FsStateBackend("hdfs://"));
        env.setStateBackend(new RocksDBStateBackend(""));
        // 检查点配置
        env.enableCheckpointing(300, CheckpointingMode.EXACTLY_ONCE);
        //高级选项
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointInterval(300);
        checkpointConfig.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        checkpointConfig.setCheckpointTimeout(600000L);
        checkpointConfig.setMaxConcurrentCheckpoints(2);//最大同时进行的checkpoint
        checkpointConfig.setMinPauseBetweenCheckpoints(100L);
        checkpointConfig.setPreferCheckpointForRecovery(false);//true倾向于检查点恢复(即使savepoint更近)
        checkpointConfig.setTolerableCheckpointFailureNumber(2);//容忍checkpoint失败次数
        // 重启策略配置
        //    每10s尝试重启，3次后失败
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,10000));
        //    10分钟内重启3次，每次间隔至少1分钟
        env.setRestartStrategy(RestartStrategies.failureRateRestart(3, Time.minutes(10),Time.minutes(1)));

    }

    @After
    public void exec() throws Exception {
        env.execute("state test");
    }

    //自定义MapFunction
    public static class MyCountMapper implements MapFunction<SensorReading,Integer>, ListCheckpointed<Integer>{
        // 定义一个本地变量作为算子状态
        private Integer count = 0;
        @Override
        public Integer map(SensorReading sensorReading) throws Exception {
            count++;
            return count;
        }

        @Override
        public List<Integer> snapshotState(long checkpointId, long timestamp) throws Exception {
            return Collections.singletonList(count);
        }

        @Override
        public void restoreState(List<Integer> state) throws Exception {
            for (Integer num : state) {
                count += num;
            }
        }
    }
    //自定义RichMapFunction
    public static class MyKeyCountMapper extends RichMapFunction<SensorReading,Integer>{
        private ValueState<Integer> valueState;

        //其他类型的state
        private ListState<String> listState;
        private MapState<String, Double> mapState;
        private ReducingState<SensorReading> reducingState;

        @Override
        public void open(Configuration parameters) throws Exception {
            valueState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("key-state", Integer.class));
            listState = getRuntimeContext().getListState(new ListStateDescriptor<String>("list-state", String.class));
            mapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, Double>("map-state", String.class, Double.class));
//            reducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<SensorReading>("",));
        }

        @Override
        public Integer map(SensorReading sensorReading) throws Exception {
            //listState API
            Iterable<String> strings = listState.get();
            listState.add("hello");
            listState.update(new ArrayList<>());
            //mapState API
            Double val = mapState.get("key");
            mapState.put("K", 1.1);
            mapState.remove("key");
            mapState.entries();
            //reducing API
            reducingState.add(sensorReading);

            reducingState.clear();

            Integer value = valueState.value();
            if (value == null) {
                value = 0;
            }
            value++;
            valueState.update(value);
            return value;
        }
    }

    public static class TempWarning extends RichFlatMapFunction<SensorReading, Tuple3<String, Double, Double>> {
        private Double threshold;
        private ValueState<Double> lastTemp;

        public TempWarning(Double threshold) {
            this.threshold = threshold;
        }
        @Override
        public void open(Configuration parameters) throws Exception {
            lastTemp = getRuntimeContext().getState(new ValueStateDescriptor<Double>("last-temp", Double.class));
        }

        @Override
        public void flatMap(SensorReading s, Collector<Tuple3<String, Double, Double>> collector) throws Exception {
            Double value = lastTemp.value();
            if (value != null) {
                if (Math.abs(s.getTemperature() - value) >= threshold) {
                    collector.collect(new Tuple3<>(s.getId(), value, s.getTemperature()));
                }
            }
            lastTemp.update(s.getTemperature());
        }

        @Override
        public void close() throws Exception {
            lastTemp.clear();
        }
    }
}
