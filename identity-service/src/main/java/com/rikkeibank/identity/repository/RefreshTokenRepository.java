package com.rikkeibank.identity.repository;
import com.rikkeibank.identity.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long>{ Optional<RefreshToken> findByTokenAndRevokedFalse(String token); List<RefreshToken> findAllByUsernameAndRevokedFalse(String username); }
