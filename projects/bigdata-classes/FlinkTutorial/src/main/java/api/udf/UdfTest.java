package api.udf;

import bean.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;
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
}
