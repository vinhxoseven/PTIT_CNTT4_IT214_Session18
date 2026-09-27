package com.rikkeibank.identity.config;

import com.rikkeibank.identity.model.*;
import com.rikkeibank.identity.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedUsers(UserAccountRepository users, StaffProfileRepository staff, PasswordEncoder encoder) {
        return args -> {
            createUser(users, encoder, "admin", "Admin@123", "RikkeiBank Admin", Role.ADMIN);
            createUser(users, encoder, "teller01", "Teller@123", "Teller One", Role.TELLER);
            createUser(users, encoder, "customer01", "Customer@123", "Customer One", Role.CUSTOMER);
            if (!staff.existsByEmployeeCode("NV001")) {
                StaffProfile s = new StaffProfile();
                s.setEmployeeCode("NV001"); s.setUsername("teller01"); s.setFullName("Teller One");
                s.setBranch("RikkeiBank Retail Branch"); s.setRole(Role.TELLER); staff.save(s);
            }
        };
    }
    private void createUser(UserAccountRepository repo, PasswordEncoder encoder, String username, String pass, String name, Role role) {
        if (repo.existsByUsername(username)) return;
        UserAccount u = new UserAccount(); u.setUsername(username); u.setPasswordHash(encoder.encode(pass)); u.setFullName(name); u.setRole(role); repo.save(u);
    }
}
