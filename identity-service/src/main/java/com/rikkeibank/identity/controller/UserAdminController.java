package com.rikkeibank.identity.controller;
import com.rikkeibank.identity.dto.*;
import com.rikkeibank.identity.service.UserAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/admin/users") @PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {
 private final UserAdminService service; public UserAdminController(UserAdminService service){this.service=service;}
 @GetMapping public List<UserResponse> all(){return service.all();}
 @PostMapping public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));}
 @PutMapping("/{id}") public UserResponse update(@PathVariable Long id,@Valid @RequestBody UserRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.deactivate(id);return ResponseEntity.noContent().build();}
}
