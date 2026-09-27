package com.rikkeibank.identity.service;
import com.rikkeibank.identity.dto.*;
import com.rikkeibank.identity.model.*;
import com.rikkeibank.identity.repository.*;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class AuthService {
 private final UserAccountRepository users; private final RefreshTokenRepository tokens; private final PasswordEncoder encoder;
 private final JwtTokenService jwt; private final StringRedisTemplate redis; private final Duration refreshTtl;
 public AuthService(UserAccountRepository users,RefreshTokenRepository tokens,PasswordEncoder encoder,JwtTokenService jwt,StringRedisTemplate redis,
   @Value("${app.jwt.refresh-ttl-days:7}") long days){this.users=users;this.tokens=tokens;this.encoder=encoder;this.jwt=jwt;this.redis=redis;this.refreshTtl=Duration.ofDays(days);}
 @Transactional public AuthResponse login(LoginRequest req){
   UserAccount u=users.findByUsername(req.username()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sai tài khoản hoặc mật khẩu"));
   if(!u.isActive()||!encoder.matches(req.password(),u.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sai tài khoản hoặc mật khẩu");
   redis.delete("revoked:user:"+u.getUsername());
   RefreshToken rt=new RefreshToken(); rt.setToken(UUID.randomUUID().toString());rt.setUsername(u.getUsername());rt.setExpiresAt(Instant.now().plus(refreshTtl));tokens.save(rt);
   return new AuthResponse(jwt.createAccessToken(u),rt.getToken(),"Bearer",jwt.accessTtlSeconds());
 }
 @Transactional public AuthResponse refresh(RefreshRequest req){
   RefreshToken rt=tokens.findByTokenAndRevokedFalse(req.refreshToken()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh token không hợp lệ"));
   if(rt.getExpiresAt().isBefore(Instant.now())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh token đã hết hạn");
   UserAccount u=users.findByUsername(rt.getUsername()).orElseThrow();
   if(!u.isActive()||Boolean.TRUE.equals(redis.hasKey("revoked:user:"+u.getUsername()))) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Phiên đã bị thu hồi");
   return new AuthResponse(jwt.createAccessToken(u),rt.getToken(),"Bearer",jwt.accessTtlSeconds());
 }
 @Transactional public void revoke(String username){
   tokens.findAllByUsernameAndRevokedFalse(username).forEach(t->{t.setRevoked(true);tokens.save(t);});
   redis.opsForValue().set("revoked:user:"+username,"true",Duration.ofDays(1));
 }
}
