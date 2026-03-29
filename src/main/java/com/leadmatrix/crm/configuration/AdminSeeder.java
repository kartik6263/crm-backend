/*package com.leadmatrix.crm.configuration;

import com.leadmatrix.crm.entity.databaseCRM;
import com.leadmatrix.crm.respository.crmRespository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdmin(crmRespository repo, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repo.findByEmail("admin@gmail.com").isEmpty()) {
                databaseCRM user = new databaseCRM();
                user.setName("Admin");
                user.setEmail("admin@gmail.com");
                user.setPhone(999999999);
                user.setPassword(passwordEncoder.encode("123456"));
                user.setRole("ADMIN");
                user.setCompanyId(1L);

                repo.save(user);
                System.out.println("Admin seeded successfully");
            }
        };
    }
} */
