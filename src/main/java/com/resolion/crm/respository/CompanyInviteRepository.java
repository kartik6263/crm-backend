package com.resolion.crm.respository;

import com.resolion.crm.entity.CompanyInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyInviteRepository extends JpaRepository<CompanyInvite, Long> {
    Optional<CompanyInvite> findByInviteToken(String inviteToken);
}