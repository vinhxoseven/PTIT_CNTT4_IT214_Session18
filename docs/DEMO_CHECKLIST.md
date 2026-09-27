# RikkeiBank Demo Checklist

- [ ] `docker compose ps` không có service Exit.
- [ ] Eureka dashboard thấy: API-GATEWAY, IDENTITY-SERVICE, CUSTOMER-SERVICE, ACCOUNT-SERVICE, TRANSACTION-SERVICE, NOTIFICATION-SERVICE.
- [ ] Login ADMIN thành công.
- [ ] Login CUSTOMER thành công và có refresh token.
- [ ] Guest gọi `/api/accounts/me` nhận 401.
- [ ] CUSTOMER gọi `/api/accounts/me` chỉ thấy tài khoản của mình.
- [ ] ADMIN CRUD Customer / Account Type / Account.
- [ ] Gọi Account Type 2 lần và kiểm tra Redis có cache key.
- [ ] Transfer account 1 -> 2 thành công; transaction COMPLETED.
- [ ] Notification consumer nhận `transfer.completed`.
- [ ] Transfer account 1 -> 999999; số dư nguồn được compensate; transaction FAILED; Kafka `transfer.failed`.
- [ ] Assign transaction cho teller01; teller khác không được review.
- [ ] Dừng account-service, gọi transfer để minh họa Circuit Breaker.
- [ ] ADMIN revoke customer01; token cũ bị Gateway trả 401.
- [ ] Chạy `mvn clean test` và mở JaCoCo report.
