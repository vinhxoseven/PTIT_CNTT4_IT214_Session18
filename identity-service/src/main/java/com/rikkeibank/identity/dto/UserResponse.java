package com.rikkeibank.identity.dto;
import com.rikkeibank.identity.model.Role;
public record UserResponse(Long id,String username,String fullName,Role role,boolean active){}
