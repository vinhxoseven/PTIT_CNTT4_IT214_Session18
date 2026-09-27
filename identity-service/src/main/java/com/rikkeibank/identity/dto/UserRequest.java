package com.rikkeibank.identity.dto;
import com.rikkeibank.identity.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record UserRequest(@NotBlank String username,@NotBlank String password,@NotBlank String fullName,@NotNull Role role){}
