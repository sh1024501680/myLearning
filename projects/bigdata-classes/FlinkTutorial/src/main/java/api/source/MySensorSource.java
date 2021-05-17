package api.source;

import bean.SensorReading;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class MySensorSource implements SourceFunction<SensorReading> {

    private Boolean running = true;

    @Override
    public void run(SourceContext<SensorReading> sourceContext) throws Exception {
        SensorReading sensorReading = new SensorReading();
        Random random = new Random();
        while (running) {
            sensorReading.setId("sensor_" + random.nextInt(10));
            sensorReading.setTimeStamp(System.currentTimeMillis());
            sensorReading.setTemperature(random.nextGaussian() + 30);
            sourceContext.collect(sensorReading);
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
