package orderpay_detect;

import orderpay_detect.beans.OrderEvent;
import orderpay_detect.beans.ReceiptEvent;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.net.URL;

public class TxPayMatch {
    private final static OutputTag<OrderEvent> unmatchedPays = new OutputTag<OrderEvent>("unmatched pays") {};
    private final static OutputTag<ReceiptEvent> unmatchedReceipts = new OutputTag<ReceiptEvent>("unmatchedReceipts pays") {};

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        URL orderResource = OrderPayTimeout.class.getResource("/OrderLog.csv");
        DataStream<OrderEvent> orderStream = env.readTextFile(orderResource.getPath())
                .map(line -> {
                    String[] fields = line.split(",");
                    return new OrderEvent(new Long(fields[0]), fields[1], fields[2], new Long(fields[3]));
                })
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<OrderEvent>() {
                    @Override
                    public long extractAscendingTimestamp(OrderEvent element) {
                        return element.getTimestamp() * 1000L;
                    }
                }).filter(data-> !"".equals(data.getTxId()));

        URL receiptResource = OrderPayTimeout.class.getResource("/ReceiptLog.csv");
        DataStream<ReceiptEvent> receiptDataStream = env.readTextFile(receiptResource.getPath())
                .map(line -> {
                    String[] fields = line.split(",");
                    return new ReceiptEvent(fields[0], fields[1], new Long(fields[2]));
                })
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<ReceiptEvent>() {
                    @Override
                    public long extractAscendingTimestamp(ReceiptEvent element) {
                        return element.getTimestamp() * 1000L;
                    }
                });
        SingleOutputStreamOperator<Tuple2<OrderEvent,ReceiptEvent>> result = orderStream.keyBy(OrderEvent::getTxId)
                .connect(receiptDataStream.keyBy(ReceiptEvent::getTxId))
                .process(new CoProcessFunction<OrderEvent, ReceiptEvent, Tuple2<OrderEvent,ReceiptEvent>>() {
                    ValueState<OrderEvent> payState;
                    ValueState<ReceiptEvent> receiptState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        payState = getRuntimeContext().getState(new ValueStateDescriptor<OrderEvent>("pay", OrderEvent.class));
                        receiptState = getRuntimeContext().getState(new ValueStateDescriptor<ReceiptEvent>("receipt", ReceiptEvent.class));
                    }

                    @Override
                    public void processElement1(OrderEvent value, Context ctx, Collector<Tuple2<OrderEvent,ReceiptEvent>> out) throws Exception {
                        ReceiptEvent receipt = receiptState.value();
                        if (receipt != null) {
                            // 如果receipt不为空，说明到账事件来过，输出匹配事件，清空状态
                            out.collect(new Tuple2<>(value, receipt));
                            payState.clear();
                            receiptState.clear();
                        } else {
                            // 如果receipt没来，注册一个定时器开始等待
                            ctx.timerService().registerEventTimeTimer((value.getTimestamp() + 5) * 1000L);
                            // 更新状态
                            payState.update(value);
                        }
                    }

                    @Override
                    public void processElement2(ReceiptEvent value, Context ctx, Collector<Tuple2<OrderEvent,ReceiptEvent>> out) throws Exception {
                        OrderEvent pay = payState.value();
                        if (pay != null) {
                            // 如果receipt不为空，说明到账事件来过，输出匹配事件，清空状态
                            out.collect(new Tuple2<>(pay, value));
                            payState.clear();
                            receiptState.clear();
                        } else {
                            // 如果receipt没来，注册一个定时器开始等待
                            ctx.timerService().registerEventTimeTimer((value.getTimestamp() + 3) * 1000L);
                            // 更新状态
                            receiptState.update(value);
                        }
                    }

                    @Override
                    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<OrderEvent, ReceiptEvent>> out) throws Exception {
                        //定时器触发，有可能有一个事件没来，也有可能都来过已经输出并清空状态
                        // 判断哪个不为空，那么另一个就没来
                        if (payState.value() != null) {
                            ctx.output(unmatchedPays, payState.value());
                        }
                        if (receiptState.value() != null) {
                            ctx.output(unmatchedReceipts, receiptState.value());
                        }
                        payState.clear();
                        receiptState.clear();
                    }
                });

        result.print("正常匹配");
        result.getSideOutput(unmatchedPays).print("unmatchedReceipts");
        result.getSideOutput(unmatchedReceipts).print("unmatchedReceipts");

        env.execute("对账 job");
    }
}
