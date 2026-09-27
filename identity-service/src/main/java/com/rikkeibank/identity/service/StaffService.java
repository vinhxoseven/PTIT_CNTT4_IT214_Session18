package com.rikkeibank.identity.service;

import com.rikkeibank.identity.dto.StaffRequest;
import com.rikkeibank.identity.model.Role;
import com.rikkeibank.identity.model.StaffProfile;
import com.rikkeibank.identity.repository.StaffProfileRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StaffService {
    private final StaffProfileRepository repo;
    public StaffService(StaffProfileRepository repo) { this.repo = repo; }
    public List<StaffProfile> all() { return repo.findAll(); }
    public StaffProfile one(Long id) { return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên")); }
    @Transactional
    public StaffProfile create(StaffRequest r) {
        if (r.role() == Role.CUSTOMER) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff chỉ có role ADMIN hoặc TELLER");
        StaffProfile s = new StaffProfile(); apply(s, r); return repo.save(s);
    }
    @Transactional
    public StaffProfile update(Long id, StaffRequest r) {
        if (r.role() == Role.CUSTOMER) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff chỉ có role ADMIN hoặc TELLER");
        StaffProfile s = one(id); apply(s, r); return repo.save(s);
    }
    @Transactional
    public void delete(Long id) { StaffProfile s = one(id); s.setActive(false); repo.save(s); }
    private void apply(StaffProfile s, StaffRequest r) {
        s.setEmployeeCode(r.employeeCode()); s.setUsername(r.username()); s.setFullName(r.fullName()); s.setBranch(r.branch()); s.setRole(r.role());
    }
}
