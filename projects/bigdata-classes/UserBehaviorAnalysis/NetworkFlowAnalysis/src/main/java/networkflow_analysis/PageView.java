package networkflow_analysis;

import networkflow_analysis.beans.PageViewCount;
import networkflow_analysis.beans.UserBehavior;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Random;

public class PageView {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//        env.setParallelism(1);

        DataStream<String> inputStream = env.readTextFile("D:\\学习\\尚硅谷\\笔记\\myLearning\\projects\\bigdata-classes\\UserBehaviorAnalysis\\HotItemsAnalysis\\src\\main\\resources\\UserBehavior.csv");
        DataStream<UserBehavior> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new UserBehavior(Long.parseLong(fields[0]), Long.parseLong(fields[1]), Integer.parseInt(fields[2]), fields[3], Long.parseLong(fields[4]));
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehavior>() {
            @Override
            public long extractAscendingTimestamp(UserBehavior element) {
                return element.getTimeStamp() * 1000L;
            }
        });

        // 分组开窗聚合

        SingleOutputStreamOperator<Tuple2<String, Long>> pvResultStream = dataStream.filter(data -> "pv".equals(data.getBehavior()))
//                .filter(data->{
//                    String regex = "^((?!\\.(css|js|png|ico)$).)*$";
//                    return Pattern.matches(regex, data.getUrl());
//                })
                .map(new MapFunction<UserBehavior, Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> map(UserBehavior userBehavior) throws Exception {
                        return new Tuple2<>("pv", 1L);
                    }
                })
                .keyBy(0)
                .timeWindow(Time.hours(1))
                .sum(1);

        SingleOutputStreamOperator<PageViewCount> pvStream = dataStream.filter(data -> "pv".equals(data.getBehavior()))
                .map(new MapFunction<UserBehavior, Tuple2<Integer, Long>>() {
                    @Override
                    public Tuple2<Integer, Long> map(UserBehavior userBehavior) throws Exception {
                        Random random = new Random();
                        return new Tuple2<>(random.nextInt(10), 1L);
                    }
                }).keyBy(data -> data.f0)
                .timeWindow(Time.hours(1))
                .aggregate(new AggregateFunction<Tuple2<Integer, Long>, Long, Long>() {
                    @Override
                    public Long createAccumulator() {
                        return 0L;
                    }

                    @Override
                    public Long add(Tuple2<Integer, Long> integerLongTuple2, Long aLong) {
                        return aLong + 1;
                    }

                    @Override
                    public Long getResult(Long aLong) {
                        return aLong;
                    }

                    @Override
                    public Long merge(Long aLong, Long acc1) {
                        return aLong + acc1;
                    }
                }, new WindowFunction<Long, PageViewCount, Integer, TimeWindow>() {

                    @Override
                    public void apply(Integer integer, TimeWindow timeWindow, Iterable<Long> iterable, Collector<PageViewCount> collector) throws Exception {
                        collector.collect(new PageViewCount(integer.toString(),timeWindow.getEnd(),iterable.iterator().next()));
                    }
                });

        SingleOutputStreamOperator<PageViewCount> pvResult = pvStream.keyBy(PageViewCount::getWindowEnd).process(new KeyedProcessFunction<Long, PageViewCount, PageViewCount>() {
            ValueState<Long> totalCountState;

            @Override
            public void open(Configuration parameters) throws Exception {
                totalCountState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("total-count",Long.class,0L));
            }

            @Override
            public void processElement(PageViewCount pageViewCount, Context context, Collector<PageViewCount> collector) throws Exception {
                totalCountState.update(totalCountState.value()+ pageViewCount.getCount());
                context.timerService().registerEventTimeTimer(pageViewCount.getWindowEnd() + 1);
            }

            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<PageViewCount> out) throws Exception {
                Long totalCount = totalCountState.value();
                out.collect(new PageViewCount("pv", ctx.getCurrentKey(), totalCount));
                totalCountState.clear();
            }
        });
//                .sum("count");

//        pvResultStream.print();
        pvResult.print();
        env.execute("pv count job");
    }
}
