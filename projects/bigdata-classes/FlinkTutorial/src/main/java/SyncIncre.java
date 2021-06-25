import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


/**
 * 增量同步
 */
public class SyncIncre {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env,settings);

        /*Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "centos7:9092");
        properties.setProperty("group.id", "consumer-group");
//        properties.setProperty("key.deserializer", "org.apache.kafka.common.deserialization.StringDeserializer");
//        properties.setProperty("value.deserializer", "org.apache.kafka.common.deserialization.StringDeserializer");
//        properties.setProperty("auto.offset.reset", "latest");
        FlinkKafkaConsumer myConsumer = new FlinkKafkaConsumer<>("maxwell_01",new SimpleStringSchema(),properties);
        myConsumer.setStartFromEarliest();
        DataStream<String> dataStream = env.addSource(myConsumer);
        dataStream.print();*/

        tableEnv.executeSql("CREATE TABLE maxwell_01 (\n" +
                "  id BIGINT,\n" +
                "  name STRING,\n" +
                "  age INT\n" +
                ") WITH (\n" +
                " 'connector' = 'kafka',\n" +
                " 'topic' = 'maxwell_01',\n" +
                " 'properties.bootstrap.servers' = 'centos7:9092',\n" +
                " 'properties.group.id' = 'testGroup',\n" +
//                " 'scan.startup.mode' = 'earliest-offset',\n" +
                " 'format' = 'maxwell-json'\n" +
                ")");
//        Table table = tableEnv.sqlQuery("select * from maxwell_01");
//        tableEnv.toRetractStream(table, Row.class).print();

        tableEnv.executeSql("CREATE TABLE users_flinkTable (\n" +
                " id BIGINT,\n" +
                " cf1 ROW<name STRING, age INT>,\n" +
                " PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                " 'connector' = 'hbase-2.2',\n" +
                " 'table-name' = 'users',\n" +
                " 'zookeeper.quorum' = 'centos7:2181'\n" +
                ")");


        tableEnv.executeSql("INSERT INTO users_flinkTable\n" +
                "SELECT id, ROW(name, age) FROM maxwell_01");

        env.execute("增量同步测试");
    }
}
