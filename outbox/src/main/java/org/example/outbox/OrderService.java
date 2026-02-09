package org.example.outbox;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long save(String content) {
        // 1. 비즈니스 로직 저장 (DB 트랜잭션 시작)
        Order order = orderRepository.save(new Order(null, content));
        Long id = order.getId();

        if (id % 5 == 0) {
            throw new RuntimeException("5의 배수는 안돼!!");
        }

        // 3. 동일한 트랜잭션 내에서 Outbox 저장
        String payload = toJson(new OrderEvent(id, "CREATED"));
        Outbox outbox = new Outbox("ORDER", id, "CREATED", payload);
        outboxRepository.save(outbox);

        return id;
    }

    // 객체를 JSON 문자열로 변환하는 헬퍼 메서드
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }
}
