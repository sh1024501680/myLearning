package api.source;

import bean.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessTest {
    private StreamExecutionEnvironment env;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
    }

    @Test
    public void processTest1_KeyedProcessFunction() {
        DataStream<SensorReading> dataStream = env.socketTextStream("localhost", 7777).map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));
        //先分组，然后自定义处理
        dataStream.keyBy("id")
                .process(new MyProcess());
    }

    @Test
    public void processTest2_ApplicationCase() {
        DataStream<SensorReading> dataStream = env.socketTextStream("localhost", 7777).map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));
        dataStream.keyBy("id")
                .process(new TempIncreWarning(10)).print();
    }

    @Test
    public void processTest3_SideOutputCase() {
        DataStream<SensorReading> dataStream = env.socketTextStream("localhost", 7777).map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])));
        OutputTag<SensorReading> lowTemp = new OutputTag<SensorReading>("low-Temp") {
        };
        //自定义侧输出流实现分流操作
        SingleOutputStreamOperator<SensorReading> highTemp = dataStream.process(new ProcessFunction<SensorReading, SensorReading>() {
            @Override
            public void processElement(SensorReading value, Context ctx, Collector<SensorReading> out) throws Exception {
                if (value.getTemperature() > 30) {
                    out.collect(value);
                } else {
                    ctx.output(lowTemp, value);
                }
            }
        });
        highTemp.print("high");
        highTemp.getSideOutput(lowTemp).print("low");
    }
    

    @After
    public void exec() throws Exception {
        env.execute("process test");
    }

    public static class MyProcess extends KeyedProcessFunction<Tuple,SensorReading,Integer> {
        private ValueState<Long> tsTimer;

        @Override
        public void open(Configuration parameters) throws Exception {
            tsTimer = getRuntimeContext().getState(new ValueStateDescriptor<Long>("ts-timer", Long.class));
        }

        @Override
        public void processElement(SensorReading value, Context ctx, Collector<Integer> out) throws Exception {
            out.collect(value.getId().length());

            //context
            ctx.getCurrentKey();
            ctx.timestamp();
//            ctx.output();
            ctx.timerService().currentWatermark();
            ctx.timerService().currentProcessingTime();
//            ctx.timerService().registerEventTimeTimer(ctx.timestamp() + 1000L);
            ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + 5000L);
            //更新为当前的时间戳
            tsTimer.update(ctx.timestamp());

//            ctx.timerService().deleteProcessingTimeTimer(ctx.timestamp() + 1000L);

        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<Integer> out) throws Exception {
            System.out.println(timestamp + "定时器触发");
            ctx.getCurrentKey();
//            ctx.output();
            ctx.timeDomain();
        }
    }

    public static class TempIncreWarning extends KeyedProcessFunction<Tuple, SensorReading, String> {
        private Integer interval;
        private ValueState<Long> timerTsState;
        private ValueState<Double> lastTemp;

        public TempIncreWarning(Integer in) {
            interval = in;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            timerTsState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timer-ts", Long.class));
            lastTemp = getRuntimeContext().getState(new ValueStateDescriptor<Double>("last-temp", Double.class));
        }

        @Override
        public void processElement(SensorReading value, Context ctx, Collector<String> out) throws Exception {
            Double lastT = lastTemp.value();
            Long timerTs = timerTsState.value();
            //如果温度值上升且没有定时器，注册10s后的定时器，开始等待
            if (lastT == null) {
                lastT = Double.MIN_VALUE;
            }
            if (value.getTimeStamp() > lastT && timerTs == null) {
                Long ts = ctx.timerService().currentProcessingTime() + interval * 1000L;
                ctx.timerService().registerProcessingTimeTimer(ts);
                timerTsState.update(ts);
            }
            //更新 温度状态
            lastTemp.update(value.getTemperature());
            if (value.getTimeStamp() < lastT) {
                ctx.timerService().deleteProcessingTimeTimer(timerTs);
                timerTsState.clear();
            }
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            out.collect(ctx.getCurrentKey().getField(0) + "连续" + interval + "s温度上升");
            timerTsState.clear();
        }

        @Override
        public void close() throws Exception {
            lastTemp.clear();
        }
    }
}
