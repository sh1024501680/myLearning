package loginfail_detect;

import loginfail_detect.beans.LoginEvent;
import loginfail_detect.beans.LoginFailWarning;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class LoginFail {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        URL resource = LoginFail.class.getResource("/LoginLog.csv");
        assert resource != null;
        DataStream<LoginEvent> dataStream = env.readTextFile(resource.getPath())
                .map(line->{
                    String[] fields = line.split(",");
                    return new LoginEvent(new Long(fields[0]), fields[1], fields[2], Long.parseLong(fields[3]));
                })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<LoginEvent>(Time.seconds(3)) {
                    @Override
                    public long extractTimestamp(LoginEvent element) {
                        return element.getTimestamp() * 1000L;
                    }
                });
        SingleOutputStreamOperator<LoginFailWarning> resultStream = dataStream.keyBy(LoginEvent::getUserId)
                .process(new KeyedProcessFunction<Long, LoginEvent, LoginFailWarning>() {
                    Integer maxFailTimes = 2;
                    ListState<LoginEvent> loginFailListState;
//                    ValueState<Long> timerTsState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        loginFailListState = getRuntimeContext().getListState(new ListStateDescriptor<LoginEvent>("login-fail-list", LoginEvent.class));
//                        timerTsState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timerTs", Long.class));
                    }

                    /*@Override
                    public void processElement(LoginEvent value, Context ctx, Collector<LoginFailWarning> out) throws Exception {
                        // 判断登录事件的类型
                        if ("fail".equals(value.getLoginStatus())) {
                            loginFailListState.add(value);
                            if (timerTsState.value() == null) {
                                Long ts = (value.getTimestamp() + 2) * 1000;
                                ctx.timerService().registerEventTimeTimer(ts);
                                timerTsState.update(ts);
                            }
                        } else {
                            // 登录成功，删除定时器，清空状态
                            if (timerTsState.value() != null) {
                                ctx.timerService().deleteEventTimeTimer(timerTsState.value());
                                loginFailListState.clear();
                                timerTsState.clear();
                            }
                        }
                    }

                    @Override
                    public void onTimer(long timestamp, OnTimerContext ctx, Collector<LoginFailWarning> out) throws Exception {
                        //定时器触发,2s内没有登录成功，判断失败次数
                        ArrayList<LoginEvent> loginFailEvents = Lists.newArrayList(loginFailListState.get());
                        int failTime = loginFailEvents.size();
                        if (failTime >= maxFailTimes) {
                            out.collect(new LoginFailWarning(ctx.getCurrentKey(), loginFailEvents.get(0).getTimestamp(), loginFailEvents.get(failTime - 1).getTimestamp(), "2s内登录失败" + failTime + "次"));
                        }
                        loginFailListState.clear();
                        timerTsState.clear();
                    }*/

                    @Override
                    public void processElement(LoginEvent value, Context ctx, Collector<LoginFailWarning> out) throws Exception {
                        // 判断当前事件登录状态
                        if ("fail".equals(value.getLoginStatus())) {
                            //状态中获取之前的登录失败
                            Iterator<LoginEvent> iterator = loginFailListState.get().iterator();
                            // 已经有登录失败
                            if (iterator.hasNext()) {
                                // 判断时间戳是否2s之内
                                LoginEvent event = iterator.next();
                                if (value.getTimestamp() - event.getTimestamp() <= 2) {
                                    out.collect(new LoginFailWarning(ctx.getCurrentKey(), event.getTimestamp(), value.getTimestamp(), "2s内登录失败2次"));
                                }
                                loginFailListState.clear();
                                loginFailListState.add(value);
                            } else {
                                // 没有登录失败，添加当前失败到状态
                                loginFailListState.add(value);
                            }
                        } else {
                            loginFailListState.clear();
                        }
                    }
                });
        resultStream.print();

        env.execute("login fail warning job");
    }
}
