package orderpay_detect;

import orderpay_detect.beans.OrderEvent;
import orderpay_detect.beans.OrderResult;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.PatternTimeoutFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class OrderPayTimeout {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        URL resource = OrderPayTimeout.class.getResource("/OrderLog.csv");
        DataStream<OrderEvent> dataStream = env.readTextFile(resource.getPath())
                .map(line -> {
                    String[] fields = line.split(",");
                    return new OrderEvent(new Long(fields[0]), fields[1], fields[2], new Long(fields[3]));
                })
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<OrderEvent>() {
                    @Override
                    public long extractAscendingTimestamp(OrderEvent element) {
                        return element.getTimestamp() * 1000L;
                    }
                });
        Pattern<OrderEvent, OrderEvent> orderPayPattern = Pattern.<OrderEvent>begin("create").where(new SimpleCondition<OrderEvent>() {
            @Override
            public boolean filter(OrderEvent value) throws Exception {
                return "create".equals(value.getEventType());
            }
        }).followedBy("pay").where(new SimpleCondition<OrderEvent>() {
            @Override
            public boolean filter(OrderEvent value) throws Exception {
                return "pay".equals(value.getEventType());
            }
        }).within(Time.minutes(15));

        //定义侧输出流标签，用来表示超时事件
        OutputTag<OrderResult> orderTimeoutTag = new OutputTag<OrderResult>("order-timeout") {};

        // 将pattern应用到数据流
        PatternStream<OrderEvent> patternStream = CEP.pattern(dataStream.keyBy(OrderEvent::getOrderId), orderPayPattern);

        SingleOutputStreamOperator<OrderResult> result = patternStream.select(orderTimeoutTag,
                new PatternTimeoutFunction<OrderEvent, OrderResult>() {
            @Override
            public OrderResult timeout(Map<String, List<OrderEvent>> map, long timeoutTimestamp) throws Exception {
                Long timeoutOrderId = map.get("create").iterator().next().getOrderId();
                return new OrderResult(timeoutOrderId,"timeout:"+timeoutTimestamp);
            }
        }, new PatternSelectFunction<OrderEvent, OrderResult>() {
                    @Override
                    public OrderResult select(Map<String, List<OrderEvent>> map) throws Exception {
                        Long orderId = map.get("pay").iterator().next().getOrderId();
                        return new OrderResult(orderId,"payed");
                    }
                });

        result.print("payed normally");
        result.getSideOutput(orderTimeoutTag).print("timeout");

        env.execute("");
    }
}
