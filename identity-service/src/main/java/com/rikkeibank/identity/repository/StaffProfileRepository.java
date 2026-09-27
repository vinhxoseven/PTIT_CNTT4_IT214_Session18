package com.rikkeibank.identity.repository;

import com.rikkeibank.identity.model.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {
    boolean existsByEmployeeCode(String employeeCode);
}
