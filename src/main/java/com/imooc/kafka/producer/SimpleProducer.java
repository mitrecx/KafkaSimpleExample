package com.imooc.kafka.producer;

import com.google.gson.Gson;
import com.imooc.kafka.common.MessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class SimpleProducer {

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<String, MessageEntity> kafkaTemplate;

    public void send(String topic, MessageEntity message) {
        kafkaTemplate.send(topic, message);
    }

    public void send(String topic, String key, MessageEntity entity) {
        ProducerRecord<String, MessageEntity> record = new ProducerRecord<>(
                topic,
                key,
                entity);

        long startTime = System.currentTimeMillis();

        ListenableFuture<SendResult<String, MessageEntity>> future = kafkaTemplate.send(record);
        future.addCallback(new ProducerCallback(startTime, key, entity));
    }


    @Slf4j
    static class ProducerCallback implements ListenableFutureCallback<SendResult<String, MessageEntity>> {

        private final long startTime;
        private final String key;
        private final MessageEntity message;

        private final Gson gson = new Gson();

        public ProducerCallback(long startTime, String key, MessageEntity message) {
            this.startTime = startTime;
            this.key = key;
            this.message = message;
        }


        @Override
        public void onSuccess(@Nullable SendResult<String, MessageEntity> result) {
            if (result == null) {
                return;
            }
            long elapsedTime = System.currentTimeMillis() - startTime;

            RecordMetadata metadata = result.getRecordMetadata();
            if (metadata != null) {
                StringBuilder record = new StringBuilder();
                record.append("message(")
                        .append("key = ").append(key).append(",")
                        .append("message = ").append(gson.toJson(message)).append(")")
                        .append("sent to partition(").append(metadata.partition()).append(")")
                        .append("with offset(").append(metadata.offset()).append(")")
                        .append("in ").append(elapsedTime).append(" ms");
                log.info(record.toString());
            }
        }

        @Override
        public void onFailure(Throwable ex) {
            ex.printStackTrace();
        }
    }
}