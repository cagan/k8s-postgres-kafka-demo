package com.demo.app.service;

import com.demo.app.config.KafkaConfig;
import com.demo.app.model.Message;
import com.demo.app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final MessageRepository messageRepository;

    @KafkaListener(topics = KafkaConfig.TOPIC_NAME, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String content) {
        log.info("Received message from Kafka: {}", content);

        Message message = new Message();
        message.setContent(content);
        message.setSource("kafka");

        messageRepository.save(message);
        log.info("Message saved to database");
    }
}
