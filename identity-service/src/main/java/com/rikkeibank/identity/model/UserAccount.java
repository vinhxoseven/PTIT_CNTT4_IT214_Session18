package com.rikkeibank.identity.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="user_accounts")
public class UserAccount {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=80) private String username;
    @Column(nullable=false) private String passwordHash;
    @Column(nullable=false,length=120) private String fullName;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Role role;
    @Column(nullable=false) private boolean active=true;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getUsername(){return username;} public void setUsername(String username){this.username=username;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String passwordHash){this.passwordHash=passwordHash;}
    public String getFullName(){return fullName;} public void setFullName(String fullName){this.fullName=fullName;}
    public Role getRole(){return role;} public void setRole(Role role){this.role=role;}
    public boolean isActive(){return active;} public void setActive(boolean active){this.active=active;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
}
