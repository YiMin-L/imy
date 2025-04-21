package com.example.common.kafka;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class KafkaProducerUtil {
    private KafkaProducer<String, String> producer;

    public KafkaProducerUtil(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    public void sendMessage(String topic, String key, String value){
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        try {
            RecordMetadata recordMetadata = producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        log.info("Sent message to topic: " + metadata.topic() + " partition: " + metadata.partition() + " offset: " + metadata.offset());
                    }
                }
            }).get();
            log.info("recordMetadata {}", JSONObject.toJSONString(recordMetadata));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        producer.close();
    }

    public static void main(String[] args) {
        KafkaProducerUtil util = new KafkaProducerUtil("110.41.140.29:9092");
        util.sendMessage("test-topic","test","test");
    }
}
