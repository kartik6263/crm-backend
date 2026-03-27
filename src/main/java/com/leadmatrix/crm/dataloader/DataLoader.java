/*package com.leadmatrix.crm.dataloader;

import com.leadmatrix.crm.entity.databaseCRM;
import com.leadmatrix.crm.respository.crmRespository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    @Autowired
    private crmRespository repo;

    @Autowired
    private PasswordEncoder encoder;

    @PostConstruct
    public void loadData() {

        if (repo.findByEmail("admin@gmail.com").isEmpty()) {
            databaseCRM user = new databaseCRM();
            user.setName("Admin");
            user.setEmail("admin@gmail.com");
            user.setPassword(encoder.encode("123456"));
            user.setRole("ADMIN");
            user.setPhone(999999999);

            repo.save(user);

            System.out.println("Admin user created");
        }
    }
}*/
