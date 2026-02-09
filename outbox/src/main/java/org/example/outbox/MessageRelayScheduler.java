package org.example.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRelayScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 1초마다 실행 (밀리세컨드 단위)
     * 고정 지연(fixedDelay) 방식을 사용하여 이전 작업이 끝난 후 1초 뒤에 다시 실행됨
     */
    @Scheduled(fixedDelay = 1000)
    @Transactional // 상태 변경(markDone)을 위해 트랜잭션 필요
    public void relayMessages() {
        // 1. 전송 대기 상태(INIT)인 메시지를 생성 시간 순으로 조회
        List<Outbox> waitingMessages = outboxRepository.findAllByStatusOrderByCreatedAtAsc("INIT");

        if (waitingMessages.isEmpty()) {
            return;
        }

        log.info("중계 대기 메시지 발견: {} 건", waitingMessages.size());

        for (Outbox outbox : waitingMessages) {
            try {
                // 2. 카프카로 메시지 전송
                // outbox.getPayload()는 이미 JSON 문자열 형태임
                kafkaTemplate.send("order-events", outbox.getPayload());

                // 3. 전송 성공 시 상태를 DONE으로 변경
                outbox.markDone();

                log.info("메시지 중계 성공 - ID: {}, AggregateId: {}", outbox.getId(), outbox.getAggregateId());
            } catch (Exception e) {
                log.error("메시지 전송 중 오류 발생 - ID: {}, 사유: {}", outbox.getId(), e.getMessage());
                break;
            }
        }
    }
}
