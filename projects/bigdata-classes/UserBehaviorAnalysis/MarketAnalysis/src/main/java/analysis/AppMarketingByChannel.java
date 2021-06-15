package analysis;

import analysis.beans.ChannelPromotionCount;
import analysis.beans.MarketingUserBehavior;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AppMarketingByChannel {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        DataStream<MarketingUserBehavior> dataStream = env.addSource(new SourceFunction<MarketingUserBehavior>() {
            Boolean running = true;

            // 定义用户行为和渠道的范围
            List<String> behaviorList = Arrays.asList("CLICK", "DOWNLOAD", "INSTALL", "UNINSTALL");
            List<String> channelList = Arrays.asList("app store", "wechat", "weibo");
            Random random = new Random();

            @Override
            public void run(SourceContext<MarketingUserBehavior> sourceContext) throws Exception {
                while (running) {
                    Long id = random.nextLong();
                    String behavior = behaviorList.get(random.nextInt(behaviorList.size()));
                    String channel = channelList.get(random.nextInt(channelList.size()));
                    Long timestamp = System.currentTimeMillis();

                    // 发送数据
                    sourceContext.collect(new MarketingUserBehavior(id, behavior, channel, timestamp));
                    Thread.sleep(100L);
                }
            }

            @Override
            public void cancel() {

            }
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<MarketingUserBehavior>() {
            @Override
            public long extractAscendingTimestamp(MarketingUserBehavior marketingUserBehavior) {
                return marketingUserBehavior.getTimestamp();
            }
        });

        SingleOutputStreamOperator<ChannelPromotionCount> result = dataStream.filter(data -> !"UNINSTALL".equals(data.getBehavior()))
                .keyBy("channel", "behavior")
                .timeWindow(Time.hours(1), Time.seconds(5))
                .aggregate(new MarketingCountAgg(), new MarketingCountResult());
        result.print();

        env.execute("app marketing by channel job");
    }

    public static class MarketingCountAgg implements AggregateFunction<MarketingUserBehavior, Long, Long> {

        @Override
        public Long createAccumulator() {
            return 0L;
        }

        @Override
        public Long add(MarketingUserBehavior marketingUserBehavior, Long aLong) {
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
    }

    public static class MarketingCountResult extends ProcessWindowFunction<Long, ChannelPromotionCount, Tuple, TimeWindow> {

        @Override
        public void process(Tuple tuple, Context context, Iterable<Long> iterable, Collector<ChannelPromotionCount> collector) throws Exception {
            String channel = tuple.getField(0);
            String behavior = tuple.getField(1);
            String windowEnd = new Timestamp(context.window().getEnd()).toString();
            Long count = iterable.iterator().next();
            collector.collect(new ChannelPromotionCount(channel,behavior,windowEnd,count));
        }
    }
}
