package com.rikkeibank.identity.config;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret){
   SecretKey key=new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
   return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
 }
 @Bean JwtAuthenticationConverter jwtAuthenticationConverter(){
   JwtGrantedAuthoritiesConverter gac=new JwtGrantedAuthoritiesConverter(); gac.setAuthoritiesClaimName("roles"); gac.setAuthorityPrefix("ROLE_");
   JwtAuthenticationConverter c=new JwtAuthenticationConverter(); c.setJwtGrantedAuthoritiesConverter(gac); return c;
 }
 @Bean SecurityFilterChain filterChain(HttpSecurity http,JwtAuthenticationConverter converter)throws Exception{
   http.csrf(csrf->csrf.disable()).authorizeHttpRequests(a->a
      .requestMatchers("/api/auth/login","/api/auth/refresh","/actuator/health").permitAll()
      .anyRequest().authenticated())
      .oauth2ResourceServer(o->o.jwt(j->j.jwtAuthenticationConverter(converter)));
   return http.build();
 }
}
