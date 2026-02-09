package org.example.outboxpush;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "outbox") // 예약어 충돌 방지를 위해 명시적 지정
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType; // 도메인 종류 (예: ORDER, USER)
    private Long aggregateId;     // 도메인 ID (예: orderId)
    private String eventType;     // 이벤트 종류 (예: CREATED, CANCELLED)

    @Column(columnDefinition = "TEXT") // 긴 메시지 본문을 저장하기 위해 TEXT 타입 사용
    private String payload;       // JSON 형태의 실제 메시지 내용

    private String status;        // 상태: INIT(전송대기), DONE(완료), FAIL(실패)

    private LocalDateTime createdAt;

    public Outbox(String aggregateType, Long aggregateId, String eventType, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = "INIT"; // 초기값은 전송 대기
        this.createdAt = LocalDateTime.now();
    }

    // 전송 완료 후 상태 변경을 위한 메서드
    public void markDone() {
        this.status = "DONE";
    }
}
