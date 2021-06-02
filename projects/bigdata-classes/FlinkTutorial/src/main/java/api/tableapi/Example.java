package api.tableapi;

import bean.SensorReading;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Example {
    private StreamExecutionEnvironment env;
    private DataStream<String> stream;
    private StreamTableEnvironment tableEnv;

    @Before
    public void getEnv() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        tableEnv = StreamTableEnvironment.create(env);
        env.setParallelism(1);
        String inPath = "src\\main\\resources\\sensor.csv";
        stream = env.readTextFile(inPath);
    }

    @Test
    public void tableTest1_ApplicationCase(){
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

    @Test
    public void tableTest2_CommonApi(){
//        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        // 1.1 基于老版本 planner 的流处理
        /*EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useOldPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment.create(env, settings);*/
        // 1.2 基于老版本 planner 的流处理
        ExecutionEnvironment batchEnv = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment batchTableEnv = BatchTableEnvironment.create(batchEnv);
        // 1.3 基于Blink的流处理
        EnvironmentSettings blinkStreamSettings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment blinkStreamTableEnv = StreamTableEnvironment.create(env, blinkStreamSettings);
        // 1.4 基于Blink的批处理
        EnvironmentSettings blinkBatchSettings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inBatchMode()
                .build();
        TableEnvironment blinkBatchTableEnv = TableEnvironment.create(blinkBatchSettings);
        // 2.表的创建  连接外部系统，读取数据
        // 2.1 读取文件
        String inPath = "src\\main\\resources\\sensor.csv";
        tableEnv.connect(new FileSystem().path(inPath))
                .withFormat(new Csv())
                .withSchema(new Schema()
                        .field("id", DataTypes.STRING())
                        .field("timeStamp",DataTypes.BIGINT())
                        .field("temperature",DataTypes.DOUBLE())
                ).createTemporaryTable("inputTable");
        Table inputTable = tableEnv.from("inputTable");
//        inputTable.printSchema();
//        tableEnv.toAppendStream(inputTable, Row.class).print();
        // 3.查询转换
        // 3.1 Table APT
        //简单转换
        Table resultTable = inputTable.select("id,temperature")
                .filter("id === sensor_6");
        // 聚合统计
        Table aggTable = inputTable.groupBy("id")
                .select("id,id.count as count,temperature.avg as avgTemp");
        // SQL
        Table res1Tbl = tableEnv.sqlQuery("select id,temperature from inputTable where id='sensor_6'");

    }

    @Test
    public void TableTest3_OutFile() {
//        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        String inPath = "src\\main\\resources\\sensor.csv";
        tableEnv.connect(new FileSystem().path(inPath))
                .withFormat(new Csv())
                .withSchema(new Schema()
                        .field("id", DataTypes.STRING())
                        .field("timeStamp",DataTypes.BIGINT())
                        .field("temperature",DataTypes.DOUBLE())
                ).createTemporaryTable("inputTable");
        Table inputTable = tableEnv.from("inputTable");
        Table resultTable = inputTable.select("id,temperature").filter("id = 'sensor_5'");

        String outPath = "src\\main\\resources\\out.txt";
        tableEnv.connect(new FileSystem().path(outPath))
                .withFormat(new Csv())
                .withSchema(new Schema()
                        .field("id", DataTypes.STRING())
                        .field("temperature",DataTypes.DOUBLE())
                ).createTemporaryTable("outputTable");
        tableEnv.toAppendStream(resultTable, Row.class).print();
        resultTable.executeInsert("outputTable");

    }

    @Test
    public void tableTest5_TimeAndWindow() {
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<SensorReading> dataStream = stream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        Table dataTable = tableEnv.fromDataStream(dataStream, "id,timeStamp as ts,temperature as temp,pt.proctime");
        Table dataTable1 = tableEnv.fromDataStream(dataStream, "id,timeStamp as ts,temperature as temp,rt.rowtime");
//        dataTable.printSchema();
//        tableEnv.toAppendStream(dataTable, Row.class).print();
        // 窗口操作
        dataTable1.printSchema();
        /*Table resultTable = dataTable1.window(Tumble.over("10.seconds").on("rt").as("tw"))
                .groupBy("id,tw")
                .select("id,id.count,temp.avg,tw.end");

        tableEnv.createTemporaryView("sensor",dataTable1);
        Table resultSqlTable = tableEnv.sqlQuery("select id,count(id) as cnt,avg(temp) as avgTemp,tumble_end(rt,interval '10' second )" +
                " from sensor group by id,tumble(rt,interval '10' second )");
        tableEnv.toRetractStream(resultSqlTable, Row.class).print();*/
    }

    @After
    public void exec() throws Exception {
        env.execute("example test");
    }
}
