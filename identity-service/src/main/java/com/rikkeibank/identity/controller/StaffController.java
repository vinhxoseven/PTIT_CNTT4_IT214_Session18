package com.rikkeibank.identity.controller;

import com.rikkeibank.identity.dto.StaffRequest;
import com.rikkeibank.identity.model.StaffProfile;
import com.rikkeibank.identity.service.StaffService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasRole('ADMIN')")
public class StaffController {
    private final StaffService service;
    public StaffController(StaffService service) { this.service = service; }
    @GetMapping public List<StaffProfile> all() { return service.all(); }
    @GetMapping("/{id}") public StaffProfile one(@PathVariable Long id) { return service.one(id); }
    @PostMapping public ResponseEntity<StaffProfile> create(@Valid @RequestBody StaffRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r)); }
    @PutMapping("/{id}") public StaffProfile update(@PathVariable Long id, @Valid @RequestBody StaffRequest r) { return service.update(id, r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
