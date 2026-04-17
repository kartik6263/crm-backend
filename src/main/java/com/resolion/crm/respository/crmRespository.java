package com.resolion.crm.respository;

import com.resolion.crm.entity.databaseCRM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface crmRespository extends JpaRepository<databaseCRM, Long> {
    Optional<databaseCRM> findByEmail(String email);

}
