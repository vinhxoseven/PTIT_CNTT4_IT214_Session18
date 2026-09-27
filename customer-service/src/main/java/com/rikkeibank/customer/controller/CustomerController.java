package com.rikkeibank.customer.controller;
import com.rikkeibank.customer.dto.CustomerRequest; import com.rikkeibank.customer.model.Customer; import com.rikkeibank.customer.service.CustomerService; import jakarta.validation.Valid; import java.util.List; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.security.oauth2.jwt.Jwt; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/customers") public class CustomerController{private final CustomerService service;public CustomerController(CustomerService s){service=s;}
 @GetMapping("/me") @PreAuthorize("hasRole('CUSTOMER')") public Customer me(@AuthenticationPrincipal Jwt jwt){return service.findMe(jwt.getSubject());}
 @GetMapping @PreAuthorize("hasRole('ADMIN')") public List<Customer> all(){return service.findAll();}
 @GetMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Customer one(@PathVariable Long id){return service.findById(id);}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Customer> create(@Valid @RequestBody CustomerRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Customer update(@PathVariable Long id,@Valid @RequestBody CustomerRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}}
