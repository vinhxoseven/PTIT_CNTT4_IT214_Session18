package com.rikkeibank.identity.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name="refresh_tokens")
public class RefreshToken {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=80) private String token;
 @Column(nullable=false,length=80) private String username;
 @Column(nullable=false) private Instant expiresAt;
 @Column(nullable=false) private boolean revoked;
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getToken(){return token;} public void setToken(String token){this.token=token;}
 public String getUsername(){return username;} public void setUsername(String username){this.username=username;}
 public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant expiresAt){this.expiresAt=expiresAt;}
 public boolean isRevoked(){return revoked;} public void setRevoked(boolean revoked){this.revoked=revoked;}
}
