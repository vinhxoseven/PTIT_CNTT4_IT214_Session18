package com.rikkeibank.identity.service;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.rikkeibank.identity.model.UserAccount;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
@Service
public class JwtTokenService {
 private final JwtEncoder encoder; private final Duration accessTtl;
 public JwtTokenService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.access-ttl-minutes:15}") long minutes){
   SecretKey key=new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
   this.encoder=new NimbusJwtEncoder(new ImmutableSecret<>(key)); this.accessTtl=Duration.ofMinutes(minutes);
 }
 public String createAccessToken(UserAccount user){
   Instant now=Instant.now();
   JwtClaimsSet claims=JwtClaimsSet.builder().issuer("rikkeibank-identity").issuedAt(now).expiresAt(now.plus(accessTtl))
      .subject(user.getUsername()).id(UUID.randomUUID().toString()).claim("roles", List.of(user.getRole().name())).claim("token_type","access").build();
   JwsHeader header=JwsHeader.with(MacAlgorithm.HS256).build();
   return encoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue();
 }
 public long accessTtlSeconds(){return accessTtl.toSeconds();}
}
