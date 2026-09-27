# Validation Notes

Đã kiểm tra tự động trong môi trường tạo artifact:

- Tất cả `pom.xml` parse XML thành công.
- Tất cả `.yml` parse YAML thành công.
- Postman collection parse JSON thành công.
- 89+ Java source/test files được quét bằng `javac`; không phát hiện lỗi cú pháp kiểu `illegal start`, `; expected`, `unclosed`, `reached end of file`.
- Đối chiếu Spring Cloud 2025.0.3 với Spring Boot 3.5.15 và Gateway 4.3.x; Gateway config dùng namespace `spring.cloud.gateway.routes` tương ứng nhánh 4.3.

Giới hạn môi trường: không có Maven/Docker daemon và không có DNS outbound cho Maven Central, vì vậy không thể chạy `mvn clean test` hay `docker compose up` ngay trong môi trường tạo ZIP. README có lệnh để build/test trên máy có Docker Desktop/Maven.
