# Architecture – RikkeiBank API

## Vì sao chọn Microservice

RikkeiBank tách domain thành các bounded context độc lập để customer/profile, account/balance, authentication, transaction và notification có thể phát triển và scale riêng. So với monolith, cách này giảm coupling triển khai, cô lập lỗi tốt hơn và phù hợp yêu cầu Database-per-service. Đổi lại hệ thống phải xử lý service discovery, network failure, eventual consistency và observability.

## Service ownership

- identity-service: user credentials, roles, refresh tokens, staff catalog.
- customer-service: customer profile.
- account-service: account type, bank account, balance và idempotent account operations.
- transaction-service: transaction record, debit/credit ledger entries, Saga orchestration.
- notification-service: asynchronous notification events.

Mỗi service không đọc database của service khác.

## Transfer Saga – Orchestrator

1. transaction-service tạo transaction PENDING + operationId.
2. Feign gọi account-service DEBIT tài khoản nguồn.
3. Feign gọi account-service CREDIT tài khoản đích.
4. Nếu thành công: transaction COMPLETED, tạo ledger DEBIT/CREDIT, publish `transfer.completed`.
5. Nếu lỗi sau DEBIT: gọi `COMPENSATE_DEBIT`.
6. Nếu lỗi sau CREDIT: `COMPENSATE_CREDIT` rồi `COMPENSATE_DEBIT`.
7. Ghi transaction FAILED và publish `transfer.failed`.

`operationId + operationType` là unique để giảm nguy cơ apply cùng operation nhiều lần.

## Cache strategy

Redis dùng Cache-Aside qua Spring Cache cho dữ liệu đọc nhiều/thay đổi ít (Customer, Account Type). Số dư không cache trong transfer path để hạn chế stale balance.

## Security

- identity-service phát JWT access token ngắn hạn + refresh token dài hơn.
- Gateway và downstream service validate chữ ký HMAC JWT.
- `@PreAuthorize` thực thi quyền theo role.
- Force logout: identity-service revoke refresh tokens và ghi Redis `revoked:user:{username}`; Gateway trả 401 cho access token còn sống của user đó.
- INTERNAL account operations không route ra Gateway và yêu cầu `X-Internal-Key`.

## Fault tolerance

transaction-service bọc Feign account calls bằng Resilience4j Circuit Breaker. Khi account-service lỗi liên tiếp, breaker chuyển CLOSED -> OPEN -> HALF_OPEN và fallback trả 503 thay vì giữ thread gọi downstream liên tục.
