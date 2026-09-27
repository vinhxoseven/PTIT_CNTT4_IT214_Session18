package com.rikkeibank.identity.service;
import com.rikkeibank.identity.dto.*;
import com.rikkeibank.identity.model.UserAccount;
import com.rikkeibank.identity.repository.UserAccountRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class UserAdminService {
 private final UserAccountRepository repo; private final PasswordEncoder encoder;
 public UserAdminService(UserAccountRepository repo,PasswordEncoder encoder){this.repo=repo;this.encoder=encoder;}
 public List<UserResponse> all(){return repo.findAll().stream().map(this::map).toList();}
 @Transactional public UserResponse create(UserRequest r){ if(repo.existsByUsername(r.username())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Username đã tồn tại"); UserAccount u=new UserAccount();u.setUsername(r.username());u.setPasswordHash(encoder.encode(r.password()));u.setFullName(r.fullName());u.setRole(r.role());return map(repo.save(u)); }
 @Transactional public UserResponse update(Long id,UserRequest r){UserAccount u=repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy user"));u.setFullName(r.fullName());u.setRole(r.role()); if(r.password()!=null&&!r.password().isBlank())u.setPasswordHash(encoder.encode(r.password())); return map(repo.save(u));}
 @Transactional public void deactivate(Long id){UserAccount u=repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy user"));u.setActive(false);repo.save(u);}
 private UserResponse map(UserAccount u){return new UserResponse(u.getId(),u.getUsername(),u.getFullName(),u.getRole(),u.isActive());}
}
