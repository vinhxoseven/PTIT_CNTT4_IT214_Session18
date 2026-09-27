package com.rikkeibank.identity.dto;

import com.rikkeibank.identity.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StaffRequest(
        @NotBlank String employeeCode,
        @NotBlank String username,
        @NotBlank String fullName,
        @NotBlank String branch,
        @NotNull Role role) {}
