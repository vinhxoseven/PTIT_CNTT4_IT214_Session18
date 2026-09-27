package com.rikkeibank.identity.controller;
import com.rikkeibank.identity.dto.*;
import com.rikkeibank.identity.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthService service; public AuthController(AuthService service){this.service=service;}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r){return service.login(r);}
 @PostMapping("/refresh") public AuthResponse refresh(@Valid @RequestBody RefreshRequest r){return service.refresh(r);}
 @PostMapping("/admin/revoke/{username}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Void> revoke(@PathVariable String username){service.revoke(username);return ResponseEntity.noContent().build();}
}
