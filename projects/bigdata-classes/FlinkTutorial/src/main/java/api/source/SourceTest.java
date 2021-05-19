package api.source;

import api.source.MySensorSource;
import bean.SensorReading;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

public class SourceTest {
    private StreamExecutionEnvironment env;
    private DataStream dataStream;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
    }
    @Test
    public void Source1_Collection() {
//        env.setParallelism(1);
        dataStream = env.fromCollection(Arrays.asList(
                new SensorReading("sensor_1", 1606879661L, 35.8089817),
                new SensorReading("sensor_5", 1606879663L, 36.8235217),
                new SensorReading("sensor_7", 1606879666L, 33.1342817),
                new SensorReading("sensor_10", 1606879668L, 37.3415317)
        ));
        DataStreamSource<Integer> streamSource = env.fromElements(1, 2, 3, 4, 5);
        dataStream.print("data");
        streamSource.print("int");
    }
    @Test
    public void Source2_File() {
        env.setParallelism(1);
        String inPath = "src\\main\\resources\\sensor.csv";
        dataStream = env.readTextFile(inPath);
        dataStream.print();
    }

    @Test
    public void Source3_kafka() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "centos7:9092");
        properties.setProperty("group.id", "consumer-group");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
        dataStream = env.addSource(new FlinkKafkaConsumer("sensor",new SimpleStringSchema(),properties));
        dataStream.print();
    }

    @Test
    public void source4_UDF() {
        dataStream = env.addSource(new MySensorSource());
        dataStream.print();
    }

    @After
    public void exec() throws Exception {
        env.execute("source test");
    }

}
