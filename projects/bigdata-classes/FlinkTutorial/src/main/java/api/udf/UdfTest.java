package api.udf;

import bean.SensorReading;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UdfTest {

    private StreamExecutionEnvironment env;
    private StreamTableEnvironment tableEnv;
    private DataStream<SensorReading> dataStream;

    @Before
    public void env() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        tableEnv = StreamTableEnvironment.create(env);

        env.setParallelism(1);
        dataStream = env.readTextFile("src\\main\\resources\\sensor.csv").map(
                line->{
                    String[] fields = line.split(",");
                    return new SensorReading(fields[0], Long.parseLong(fields[1]), Double.parseDouble(fields[2]));
                }
        );
    }

    @Test
    public void UdfTest1_ScalarFunction() {
        Table sensorTable = tableEnv.fromDataStream(dataStream,"id,timeStamp as ts,temperature as temp");
        HashCode hashCode = new HashCode(23);

        tableEnv.registerFunction("HashCode",hashCode);
        Table resultTable = sensorTable.select("id,ts,hashcode(id)");

        tableEnv.toAppendStream(resultTable, Row.class).print();

    }

    @Test
    public void UdfTest2_TableFunction() {
        Table sensorTable = tableEnv.fromDataStream(dataStream,"id,timeStamp as ts,temperature as temp");
        Split split = new Split("_");

        tableEnv.registerFunction("split", split);
        Table resultTable = sensorTable.joinLateral("split(id) as (word,length)").select("id,ts,word,length");

        tableEnv.createTemporaryView("sensor",sensorTable);
        Table sqlQuery = tableEnv.sqlQuery("select id,ts,word,length from sensor,lateral table(split(id)) as split_id(word,length)");

        tableEnv.toAppendStream(resultTable, Row.class).print();
        tableEnv.toAppendStream(sqlQuery, Row.class).print("sql");

    }

    @Test
    public void UdfTest3_AggregateFunction() {
        Table sensorTable = tableEnv.fromDataStream(dataStream,"id,timeStamp as ts,temperature as temp");
        AvgTemp avgTemp = new AvgTemp();

        tableEnv.registerFunction("avgTemp", avgTemp);
        Table resultTable = sensorTable.groupBy("id").aggregate("avgTemp(temp) as avgTem").select("id,avgTem");
        Table resultTable1 = sensorTable.groupBy("id").flatAggregate("avgTemp(temp) as avgTem").select("id,avgTem");

        tableEnv.createTemporaryView("sensor",sensorTable);
        Table sqlQuery = tableEnv.sqlQuery("select id,avgTemp(temp) from sensor group by id");

        tableEnv.toRetractStream(resultTable, Row.class).print();
        tableEnv.toRetractStream(sqlQuery, Row.class).print("sql");

    }
    @After
    public void execute() throws Exception{
        env.execute("udf test");
    }

    public static class HashCode extends ScalarFunction {
        private int factor = 13;
        public HashCode(int factor) {
            this.factor = factor;
        }
        public int eval(String str) {
            return str.hashCode() * factor;
        }
    }

    public static class Split extends TableFunction<Tuple2<String, Integer>> {
        private String separator = ",";

        public Split(String separator) {
            this.separator = separator;
        }
        // 必须实现一个eval 方法，没有返回值
        public void eval(String str) {
            for (String s : str.split(separator)) {
                collect(new Tuple2<>(s,s.length()));
            }
        }
    }

    public static class AvgTemp extends AggregateFunction<Double, Tuple2<Double, Integer>> {

        @Override
        public Double getValue(Tuple2<Double, Integer> accumulator) {
            return accumulator.f0 / accumulator.f1;
        }

        @Override
        public Tuple2<Double, Integer> createAccumulator() {
            return new Tuple2<>(0.0,0);
        }

        //必须实现一个 accumulate 方法，来数据之后状态更新
        public void accumulate(Tuple2<Double,Integer> acc,Double temp) {
            acc.f0 += temp;
            acc.f1 += 1;
        }
    }
}
