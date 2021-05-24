package api.tableapi;

import bean.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Example {
    private StreamExecutionEnvironment env;
    private DataStream<String> stream;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        String inPath = "src\\main\\resources\\sensor.csv";
        stream = env.readTextFile(inPath);
    }

    @Test
    public void test1(){
        env.setParallelism(1);

        SingleOutputStreamOperator<SensorReading> dataStream = stream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        // 创建表的执行环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 基于流创建一张表
        Table dataTable = tableEnv.fromDataStream(dataStream);
        // 调用table api进行转换操作
        Table table1 = dataTable.select("id,temperature").where("id='sensor_01'");

        tableEnv.createTemporaryView("sensor",dataTable);
        Table resultTable = tableEnv.sqlQuery("select id,temperature from sensor");

        tableEnv.toAppendStream(table1, Row.class).print("table1");
        tableEnv.toAppendStream(resultTable, Row.class).print("sql");

    }

    @After
    public void exec() throws Exception {
        env.execute("state test");
    }
}
