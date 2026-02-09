# outbox-sample

아웃박스 트랜잭션 패턴 실습을 위한 로컬 환경 코드입니다.

# 🏗 구조
주문 서비스 → Kafka → 알림 서비스
- outbox: Producer (주문 서비스)
- outbox-push: Consumer (알림 서비스)

# 🛠 실행 방법
Kafka 기동: outbox 폴더 내 docker-compose.yml을 사용하여 Zookeeper와 Broker를 실행합니다.

```bash
docker-compose up -d
```
서비스 실행: outbox와 outbox-push 애플리케이션을 각각 실행합니다.

# 실습 내용 
https://blog9909.tistory.com/88
