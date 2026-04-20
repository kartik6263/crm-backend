package com.resolion.crm.respository;

import com.resolion.crm.entity.TwoFactorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Long> {
    Optional<TwoFactorCode> findTopByEmailAndUsedFalseOrderByIdDesc(String email);
}
