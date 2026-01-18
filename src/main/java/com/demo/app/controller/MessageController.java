package com.demo.app.controller;

import com.demo.app.model.Message;
import com.demo.app.repository.MessageRepository;
import com.demo.app.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;
    private final KafkaProducerService kafkaProducerService;

    @GetMapping
    public List<Message> getAllMessages() {
        log.info("Fetching all messages from database");
        return messageRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createMessage(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");

        Message message = new Message();
        message.setContent(content);
        message.setSource("api");
        messageRepository.save(message);

        return ResponseEntity.ok(Map.of("status", "saved", "id", message.getId().toString()));
    }

    @PostMapping("/kafka")
    public ResponseEntity<Map<String, String>> sendToKafka(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "content is required"));
        }
        kafkaProducerService.sendMessage(content);
        return ResponseEntity.ok(Map.of("status", "sent to kafka"));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "version", "56.0"));
    }
}
