package com.rikkeibank.transaction.client; import com.rikkeibank.transaction.dto.*; import org.springframework.cloud.openfeign.FeignClient; import org.springframework.web.bind.annotation.*;
@FeignClient(name="account-service") public interface AccountClient{
 @PostMapping("/internal/accounts/debit") AccountBalanceResponse debit(@RequestHeader("X-Internal-Key") String key,@RequestBody AccountOperationRequest request);
 @PostMapping("/internal/accounts/credit") AccountBalanceResponse credit(@RequestHeader("X-Internal-Key") String key,@RequestBody AccountOperationRequest request);
 @PostMapping("/internal/accounts/compensate-debit") AccountBalanceResponse compensateDebit(@RequestHeader("X-Internal-Key") String key,@RequestBody AccountOperationRequest request);
 @PostMapping("/internal/accounts/compensate-credit") AccountBalanceResponse compensateCredit(@RequestHeader("X-Internal-Key") String key,@RequestBody AccountOperationRequest request);
}
