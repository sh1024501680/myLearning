package api.source.test;

import api.source.MySensorSource;
import bean.SensorReading;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class SinkTest {
    private StreamExecutionEnvironment env;
    private DataStream<String> stream;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        String inPath = "D:\\JavaProjects\\IdeaProject\\FlinkTutorial\\src\\main\\resources\\sensor.csv";
        stream = env.readTextFile(inPath);
    }

    @Test
    public void SinkTest1_kafka() {
//        stream.addSink(new FlinkKafkaProducer<String>("centos7-103:9092","sinkTest", new SimpleStringSchema()));

    }

    @Test
    public void SinkTest_Jdbc() {
        /*DataStream<SensorReading> dataStream = stream.map( f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])
        ));*/
        DataStream<SensorReading> dataStream = env.addSource(new MySensorSource());
        dataStream.addSink(new RichSinkFunction<SensorReading>() {
            Connection connection = null;
            PreparedStatement insertSQL = null;
            PreparedStatement updateSQL = null;

            @Override
            public void open(Configuration parameters) throws Exception {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecard_data_collect", "root", "123456");
                insertSQL = connection.prepareStatement("insert into sensor_temp (id,temp) values (?,?)");
                updateSQL = connection.prepareStatement("update sensor_temp set temp = ? where id = ?");
            }

            @Override
            public void invoke(SensorReading value, Context context) throws Exception {
                updateSQL.setDouble(1, value.getTemperature());
                updateSQL.setString(2, value.getId());
                updateSQL.execute();
                if (updateSQL.getUpdateCount() == 0) {
                    insertSQL.setString(1, value.getId());
                    insertSQL.setDouble(2, value.getTemperature());
                    insertSQL.execute();
                }
            }

            @Override
            public void close() throws Exception {
                insertSQL.close();
                updateSQL.close();
                connection.close();
            }
        });
    }

    @After
    public void exec() throws Exception {
        env.execute("transform test");
    }
}
