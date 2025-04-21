package com.lym.kafka;


import com.alibaba.fastjson2.JSONObject;
import com.example.common.constants.Constants;
import com.example.common.netty.Response;
import com.lym.protobuf.MessageSendResponseProto;
import io.netty.channel.socket.SocketChannel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Kafka管理组件
 */
public class KafkaManager {

    static class Singleton {

        static KafkaManager instance = new KafkaManager();

    }

    public static KafkaManager getInstance() {
        return Singleton.instance;
    }

    private KafkaProducer producer;

    public KafkaManager() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<String, String>(properties);
    }

    /**
     * 获取KafkaProducer
     *
     * @return
     */
    public KafkaProducer getProducer() {
        return producer;
    }

    /**
     * 初始化
     * todo 池化
     */
    public void init() {
        new Thread() {

            @Override
            public void run() {
                Properties properties = new Properties();
                properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
                properties.setProperty("group.id", "dispatcher_group");
                properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

                KafkaConsumer consumer = new KafkaConsumer<String, String>(properties);
                consumer.subscribe(Arrays.asList("send_message_response"));

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(100);

                    for (ConsumerRecord<String, String> record : records) {
                        JSONObject message = JSONObject.parseObject(record.value());
                        Integer requestType = message.getInteger("requestType");

                        if (requestType.equals(Constants.message_type_single)) {
                            long messageId = message.getLong("messageId");
                            long timestamp = message.getLong("timestamp");
                            String senderId = message.getString("senderId");
                            String receiverId = message.getString("receiverId");

                            MessageSendResponseProto.MessageSendResponse.Builder builder =
                                    MessageSendResponseProto.MessageSendResponse.newBuilder();
                            builder.setMessageId(messageId);
                            builder.setTimestamp(timestamp);
                            builder.setSenderId(senderId);
                            builder.setReceiverId(receiverId);
                            MessageSendResponseProto.MessageSendResponse messageSendResponse = builder.build();

                            Response response = new Response(
                                    Constants.APP_SDK_VERSION_1,
                                    Constants.MESSAGE_TYPE_RESPONSE,
                                    Constants.REQUEST_TYPE_SEND_MESSAGE,
                                    Constants.SEQUENCE_DEFAULT,
                                    messageSendResponse.toByteArray()
                            );

                            String gatewayChannelId = message.getString("gatewayChannelId");
                            GatewayInstanceManager gatewayInstanceManager = GatewayInstanceManager.getInstance();
                            SocketChannel gatewayInstance = gatewayInstanceManager.getGatewayInstance(gatewayChannelId);

                            gatewayInstance.writeAndFlush(response.getBuffer());
                        }
                    }
                }
            }
        }.start();
    }

}
