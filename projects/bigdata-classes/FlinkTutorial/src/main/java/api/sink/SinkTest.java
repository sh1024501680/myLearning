package api.sink;

import api.source.MySensorSource;
import bean.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkBase;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

public class SinkTest {
    private StreamExecutionEnvironment env;
    private DataStream stream;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        String inPath = "src\\main\\resources\\sensor.csv";
        stream = env.readTextFile(inPath);
    }

    @Test
    public void SinkTest1_kafka() {
        stream.addSink(new FlinkKafkaProducer<String>("centos7:9092","sinkTest", new SimpleStringSchema()));

    }
    
    @Test
    public void SinkTest_Redis(){
        //定义 jedis 连接配置
        FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder()
                .setHost("centos7")
                .setPort(6379)
                .build();

        DataStream<SensorReading> stream1 = stream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])
        ));
        stream1.addSink(new RedisSink<>(config, new RedisMapper<SensorReading>() {

            // 定义保存数据到Redis的命令，存成Hash表,Hset sensor_temp id temperature
            @Override
            public RedisCommandDescription getCommandDescription() {
                return new RedisCommandDescription(RedisCommand.HSET, "sensor_temp");
            }

            @Override
            public String getKeyFromData(SensorReading sensorReading) {
                return sensorReading.getId();
            }

            @Override
            public String getValueFromData(SensorReading sensorReading) {
                return sensorReading.getTemperature().toString();
            }
        }));
    }

    @Test
    public void SinkTest_ES(){
        DataStream<SensorReading> stream1 = stream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])
        ));

        // 定义ES的连接配置
        ArrayList<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("centos7",9200));

        stream1.addSink(new ElasticsearchSink.Builder<SensorReading>(httpHosts, new ElasticsearchSinkFunction<SensorReading>() {
            @Override
            public void process(SensorReading sensorReading, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                // 定义写入的数据 source
                HashMap<String, String> dataSource = new HashMap<>();
                dataSource.put("id",sensorReading.getId());
                dataSource.put("temp",sensorReading.getTemperature().toString());
                dataSource.put("timeStamp",sensorReading.getTimeStamp().toString());

                // 创建请求，作为向ES发起的写入命令
                IndexRequest indexRequest = Requests.indexRequest()
                        .index("sensor")
                        .type("readData")
                        .source(dataSource);
                //用 index 发送请求
                requestIndexer.add(indexRequest);

            }
        }).build());
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

    @Test
    public void SinkTest_JDBCTest(){
        DataStream<SensorReading> dataStream = stream.map((MapFunction<String, SensorReading>) f -> new SensorReading(
                f.split(",")[0],
                Long.parseLong(f.split(",")[1]),
                Double.parseDouble(f.split(",")[2])
        ));
        dataStream.addSink(JdbcSink.sink("insert into sensor_temp (id,temp) values (?,?)",
                (ps,sensor)->{
                    ps.setString(1, sensor.getId());
                    ps.setString(2, sensor.getTemperature().toString());
                },
                JdbcExecutionOptions.builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(200)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://localhost:3306/test?useSSL=false")
                        .withDriverName("com.mysql.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("123456")
                        .build()
        ));
    }

    @After
    public void exec() throws Exception {
        env.execute("Sink test");
    }
}
