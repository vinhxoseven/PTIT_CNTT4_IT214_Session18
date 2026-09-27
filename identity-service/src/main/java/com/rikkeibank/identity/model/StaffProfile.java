package com.rikkeibank.identity.model;

import jakarta.persistence.*;

@Entity
@Table(name = "staff_profiles")
public class StaffProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String employeeCode;
    @Column(nullable = false, unique = true, length = 80)
    private String username;
    @Column(nullable = false, length = 120)
    private String fullName;
    @Column(nullable = false, length = 120)
    private String branch;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
