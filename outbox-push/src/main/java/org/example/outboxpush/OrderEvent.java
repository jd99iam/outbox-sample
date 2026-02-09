package org.example.outboxpush;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderEvent {
    private Long orderId;      // 주문 번호
    private String status;     // 주문 상태 (예: CREATED, CANCELLED)
}
