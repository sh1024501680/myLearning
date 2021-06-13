package networkflow_analysis;

import networkflow_analysis.beans.PageViewCount;
import networkflow_analysis.beans.UserBehavior;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.HashSet;

public class UniqueVisitor {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

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

        SingleOutputStreamOperator<PageViewCount> uv = dataStream.filter(data -> "pv".equals(data.getBehavior()))
//                .filter(data->{
//                    String regex = "^((?!\\.(css|js|png|ico)$).)*$";
//                    return Pattern.matches(regex, data.getUrl());
//                })
                .timeWindowAll(Time.hours(1))
                .apply(new AllWindowFunction<UserBehavior, PageViewCount, TimeWindow>() {
                    @Override
                    public void apply(TimeWindow timeWindow, Iterable<UserBehavior> iterable, Collector<PageViewCount> collector) throws Exception {
                        HashSet<Long> uIds = new HashSet<>();
                        for (UserBehavior userBehavior : iterable) {
                            uIds.add(userBehavior.getUserId());
                        }
                        collector.collect(new PageViewCount("uv", timeWindow.getEnd(), (long) uIds.size()));
                    }
                });

        uv.print();

        env.execute("uv count");
    }
}
