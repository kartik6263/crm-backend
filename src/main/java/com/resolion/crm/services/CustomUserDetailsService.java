package com.resolion.crm.services;

import com.resolion.crm.entity.databaseCRM;
import com.resolion.crm.respository.crmRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private crmRespository CrmRespository; // ya jo tera repo hai

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        databaseCRM user = CrmRespository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole()) // ADMIN / USER etc // no .name()
                .build();
    }
}
