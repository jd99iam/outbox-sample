package org.example.outboxpush;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void handleOrderEvent(String message) { // 👈 String으로 받음
        try {
            // 따옴표가 겹쳐진 문자열일 경우를 대비해 처리
            System.out.println(message);
        } catch (Exception e) {
            System.err.println("메시지 변환 실패: " + message);
        }
    }
}
