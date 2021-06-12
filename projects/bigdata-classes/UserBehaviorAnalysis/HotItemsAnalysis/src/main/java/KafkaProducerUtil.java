import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

public class KafkaProducerUtil {
    public static void main(String[] args) throws Exception {
        writeToKafka("hotitems");
    }

    public static void writeToKafka(String topic) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "centos7:9092");
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //定义一个 KafkaProducer
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        BufferedReader bufferedReader = new BufferedReader(new FileReader("E:\\笔记和文档\\myLearning\\projects\\bigdata-classes\\UserBehaviorAnalysis\\HotItemsAnalysis\\src\\main\\resources\\UserBehavior.csv"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, line);
            //用producer 发送数据
            kafkaProducer.send(producerRecord);
        }
        kafkaProducer.close();
    }
}
