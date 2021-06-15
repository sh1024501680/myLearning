package loginfail_detect;

import loginfail_detect.beans.LoginEvent;
import loginfail_detect.beans.LoginFailWarning;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class LoginFailWithCEP {
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
        //1.定义匹配模式
        // firstFail -> secondFail, within 2s
        Pattern<LoginEvent, LoginEvent> loginFailPattern = Pattern.<LoginEvent>begin("firstFail").where(new SimpleCondition<LoginEvent>() {
            @Override
            public boolean filter(LoginEvent value) throws Exception {
                return "fail".equals(value.getLoginStatus());
            }
        }).next("secondFail").where(new SimpleCondition<LoginEvent>() {
            @Override
            public boolean filter(LoginEvent value) throws Exception {
                return "fail".equals(value.getLoginStatus());
            }
        }).within(Time.seconds(2));

        Pattern<LoginEvent, LoginEvent> loginFailPattern1 = Pattern.<LoginEvent>begin("firstFail").where(new SimpleCondition<LoginEvent>() {
            @Override
            public boolean filter(LoginEvent value) throws Exception {
                return "fail".equals(value.getLoginStatus());
            }
        }).within(Time.seconds(2))
                .consecutive() // 连续
                .times(3);//循环模式
        //2.将匹配模式应用到数据流上，得到一个pattern stream
        PatternStream<LoginEvent> patternStream = CEP.pattern(dataStream.keyBy(LoginEvent::getUserId), loginFailPattern);

        //3.从检出符合匹配条件的复杂事件进行转化处理，得到报警信息
        SingleOutputStreamOperator<LoginFailWarning> warningStream = patternStream.select(new PatternSelectFunction<LoginEvent, LoginFailWarning>() {
            @Override
            public LoginFailWarning select(Map<String, List<LoginEvent>> map) throws Exception {
                LoginEvent firstFailEvent = map.get("firstFail").get(0);
                LoginEvent secondFailEvent = map.get("secondFail").iterator().next();
                return new LoginFailWarning(firstFailEvent.getUserId(), firstFailEvent.getTimestamp(), secondFailEvent.getTimestamp(), "2s失败了2次");
            }
        });

        warningStream.print();
        env.execute("login fail warning job");
    }
}
