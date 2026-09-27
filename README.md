# RikkeiBank API – Spring Boot Microservices

Bộ source tham khảo/đồ án cho đề bài **Ngân hàng số RikkeiBank API**. Project được tổ chức dạng Maven multi-module, Java 17, Spring Boot 3.5.x và Spring Cloud 2025.0.x.

## 1. Các module

| Module | Port local | Vai trò |
|---|---:|---|
| config-server | 8888 | Spring Cloud Config (native `config-repo`) |
| discovery-server | 8761 | Eureka Service Registry |
| api-gateway | 8080 | Gateway, JWT validation, revocation check |
| identity-service | 8081 | Login, JWT, refresh token, ADMIN/TELLER/CUSTOMER, force logout |
| customer-service | 8082 | CRUD Customer, self profile, Redis Cache-Aside |
| account-service | 8083 | Account Type, Bank Account, balance debit/credit, Redis |
| transaction-service | 8084 | Transfer, Saga Orchestrator, Feign, Circuit Breaker, Kafka producer |
| notification-service | 8085 | WebFlux + Kafka consumer, notification event stream |

Database-per-service:

- `identity_db` – MySQL host port 3307
- `customer_db` – MySQL host port 3308
- `account_db` – MySQL host port 3309
- `transaction_db` – MySQL host port 3310

Không service nào truy cập trực tiếp database của service khác.

## 2. Thành phần yêu cầu đã triển khai

- RESTful JSON API + HTTP status.
- Spring Cloud Config.
- Eureka Discovery.
- Spring Cloud Gateway + `lb://` routes + LoadBalancer.
- OpenFeign cho `transaction-service -> account-service`.
- Kafka event `transfer.completed` / `transfer.failed`.
- WebFlux ở `notification-service`.
- Resilience4j Circuit Breaker `accountService` với CLOSED / OPEN / HALF_OPEN.
- Redis + Spring Cache: `@Cacheable`, `@CachePut`, `@CacheEvict`.
- Cached object implements `Serializable` và `serialVersionUID`.
- JWT + `@PreAuthorize` theo ADMIN/TELLER/CUSTOMER.
- Refresh token 7 ngày để duy trì đăng nhập.
- ADMIN force logout: revoke refresh tokens + Redis key `revoked:user:{username}`; Gateway chặn access token hiện tại.
- Saga Orchestrator trong `transaction-service`: DEBIT -> CREDIT -> COMPLETED; khi lỗi phát compensating CREDIT/DEBIT tương ứng.
- Idempotency cho debit/credit/compensation bằng `operationId`.
- Pessimistic lock + `@Version` giúp giảm rủi ro cập nhật số dư đồng thời.
- Transaction ledger: `transaction_entries` ghi `DEBIT` và `CREDIT` sau giao dịch thành công.
- TELLER chỉ xem giao dịch được assign cho chính mình và chỉ review giao dịch của mình.
- Global exception response + AOP logging lỗi service.
- Unit test service/controller mẫu + JaCoCo.
- Postman collection ở `postman/RikkeiBank.postman_collection.json`.

## 3. Tài khoản demo

Được seed tự động bởi `identity-service`:

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@123` | ADMIN |
| `teller01` | `Teller@123` | TELLER |
| `customer01` | `Customer@123` | CUSTOMER |

**Chỉ dùng cho bài tập/demo.** Khi triển khai thật phải thay password, JWT secret, internal key và dùng secret manager.

## 4. Cách chạy nhanh nhất bằng Docker Compose

Yêu cầu: Docker Desktop đang chạy.

Nếu trước đó bạn đã chạy Redis `restaurant-redis` ở cổng 6379 thì có thể giữ nguyên; Redis trong compose không bind cổng 6379 ra host nên không xung đột.

Tại thư mục gốc project:

```powershell
docker compose up -d --build
```

Theo dõi:

```powershell
docker compose ps
docker compose logs -f config-server discovery-server api-gateway
```

Sau khi các service lên:

- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8080`
- Config Server test: `http://localhost:8888/account-service/default`

Dừng:

```powershell
docker compose down
```

Xóa luôn dữ liệu DB nếu muốn làm mới hoàn toàn:

```powershell
docker compose down -v
```

## 5. Chạy bằng IntelliJ

Nếu chạy service trực tiếp trên máy thay vì Docker, cần có MySQL tại các port 3307-3310, Redis tại 6379 và Kafka tại 9092.

Thứ tự chạy:

1. `ConfigServerApplication`
2. `DiscoveryServerApplication`
3. `ApiGatewayApplication`
4. `IdentityServiceApplication`
5. `CustomerServiceApplication`
6. `AccountServiceApplication`
7. `TransactionServiceApplication`
8. `NotificationServiceApplication`

Các client dùng `spring.config.import=configserver:...`, do đó **Config Server phải chạy trước**.

## 6. Flow đăng nhập

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "customer01",
  "password": "Customer@123"
}
```

Copy `accessToken`, sau đó gửi:

```text
Authorization: Bearer <accessToken>
```

Refresh token:

```http
POST /api/auth/refresh
{
  "refreshToken": "..."
}
```

ADMIN buộc customer đăng xuất:

```http
POST /api/auth/admin/revoke/customer01
Authorization: Bearer <ADMIN_TOKEN>
```

Sau đó request đang dùng token cũ của `customer01` qua Gateway nhận `401`.

## 7. Demo Redis Cache-Aside

Login ADMIN/CUSTOMER rồi gọi 2 lần:

```http
GET /api/account-types
```

`AccountTypeService` có:

```java
@Cacheable(value = "account_types", key = "#id")
@Cacheable("account_type_list")
@CachePut(...)
@CacheEvict(...)
```

`CustomerService` cũng cache `customers` và `customer_list`.

Kiểm tra Redis trong compose:

```powershell
docker exec -it rikkeibank-redis redis-cli
KEYS *
```

Không cache số dư tài khoản trong flow chuyển tiền để tránh trả số dư tài chính cũ.

## 8. Demo Saga chuyển khoản

Tài khoản seed ở `account-service`:

- ID 1 – `customer01` – 5,000,000
- ID 2 – tài khoản đích – 2,000,000

Login `customer01`, gọi:

```http
POST /api/transactions/transfers
Authorization: Bearer <CUSTOMER_TOKEN>
Content-Type: application/json

{
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "amount": 100000
}
```

Flow:

```text
transaction-service
  -> DEBIT account-service
  -> CREDIT account-service
  -> bank_transactions = COMPLETED
  -> transaction_entries: DEBIT + CREDIT
  -> Kafka: transfer.completed
  -> notification-service consumer
```

Nếu CREDIT hoặc bước sau DEBIT lỗi, orchestrator gọi compensating operation để trả số dư về trước đó và ghi transaction FAILED.

## 9. Demo rollback chủ động

Cách đơn giản: transfer tới `targetAccountId` không tồn tại, ví dụ `999999`.

```json
{
  "sourceAccountId": 1,
  "targetAccountId": 999999,
  "amount": 100000
}
```

DEBIT nguồn xảy ra trước, CREDIT đích thất bại, sau đó `COMPENSATE_DEBIT` cộng lại số tiền cho nguồn. Event `transfer.failed` được publish.

Kiểm tra notification:

```http
GET /api/notifications/events
Authorization: Bearer <ADMIN_OR_TELLER_TOKEN>
```

## 10. Demo Circuit Breaker

Dừng account service:

```powershell
docker compose stop account-service
```

Gọi transfer nhiều lần. Sau ngưỡng cấu hình, circuit breaker `accountService` chuyển trạng thái từ CLOSED -> OPEN; sau thời gian chờ sẽ sang HALF_OPEN.

Xem actuator của transaction-service khi chạy local trực tiếp:

```text
http://localhost:8084/actuator/circuitbreakers
http://localhost:8084/actuator/circuitbreakerevents
```

Trong full Docker compose, service port nghiệp vụ không được publish ra host; có thể dùng `docker exec` hoặc tạm publish port nếu cần demo actuator trực tiếp.

## 11. Phân quyền

- ADMIN: CRUD users/staff, customers, account types, bank accounts; assign teller; revoke user session.
- TELLER: chỉ xem giao dịch trong ngày đã assign cho chính mình; review bị kiểm tra `assignedTellerUsername`.
- CUSTOMER: chỉ lấy hồ sơ/tài khoản/lịch sử của chính username trong JWT; transfer nguồn được account-service xác nhận owner.
- Guest: Gateway từ chối API tài chính nếu không có JWT.

## 12. Test + JaCoCo

Khi máy có Maven:

```powershell
mvn clean test
```

Report JaCoCo của từng module nằm dưới:

```text
<module>/target/site/jacoco/index.html
```

## 13. Postman

Import file:

```text
postman/RikkeiBank.postman_collection.json
```

Collection đã có nhóm Auth, Customers, Accounts & Cache, Transactions - Saga và Notifications.

## 14. Lưu ý bảo mật

Đây là baseline học tập, không phải cấu hình production banking. Production cần TLS/mTLS, secret manager/Vault, key rotation/asymmetric JWT, audit log bất biến, rate limiting, anti-fraud, encryption at rest, observability/centralized logs, database HA/backup, Kafka ACL/SASL, và quy trình kiểm thử bảo mật/tuân thủ riêng.
