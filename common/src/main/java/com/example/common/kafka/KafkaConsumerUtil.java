package com.example.common.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
public class KafkaConsumerUtil {
    private KafkaConsumer<String, String> consumer;

    public KafkaConsumerUtil(String bootstrapServers, String groupId, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void consumeMessages() {
        while (true) {
            consumer.subscribe(Collections.singletonList("test-topic"));

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                log.info("Consumed message: key = {}, value = {}, topic = {}, partition = {}, offset = {}",
                        record.key(), record.value(), record.topic(), record.partition(), record.offset());
            }
        }
    }

    public void close() {
        consumer.close();
    }

    public static void main(String[] args) {
        KafkaConsumerUtil util = new KafkaConsumerUtil("110.41.140.29:9092","test","test-topic");
        util.consumeMessages();
    }
}
