package com.rikkeibank.customer.service;
import com.rikkeibank.customer.dto.CustomerRequest; import com.rikkeibank.customer.model.Customer; import com.rikkeibank.customer.repository.CustomerRepository; import java.util.*; import org.springframework.cache.annotation.*; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.server.ResponseStatusException;
@Service public class CustomerService{private final CustomerRepository repo;public CustomerService(CustomerRepository repo){this.repo=repo;}
 @Cacheable(value="customers",key="#id") public Customer findById(Long id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy khách hàng"));}
 public Customer findMe(String username){return repo.findByIdentityUsername(username).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy hồ sơ khách hàng"));}
 @Cacheable(value="customer_list") public List<Customer> findAll(){return repo.findAll();}
 @Transactional @Caching(put=@CachePut(value="customers",key="#result.id"),evict=@CacheEvict(value="customer_list",allEntries=true)) public Customer create(CustomerRequest r){Customer c=new Customer();apply(c,r);return repo.save(c);}
 @Transactional @Caching(put=@CachePut(value="customers",key="#id"),evict=@CacheEvict(value="customer_list",allEntries=true)) public Customer update(Long id,CustomerRequest r){Customer c=findById(id);apply(c,r);return repo.save(c);}
 @Transactional @Caching(evict={@CacheEvict(value="customers",key="#id"),@CacheEvict(value="customer_list",allEntries=true)}) public void delete(Long id){Customer c=findById(id);repo.delete(c);}
 private void apply(Customer c,CustomerRequest r){c.setIdentityUsername(r.identityUsername());c.setFullName(r.fullName());c.setEmail(r.email());c.setPhone(r.phone());}
}
