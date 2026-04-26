package com.resolion.crm.respository;

import com.resolion.crm.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByCompanyId(Long companyId);

    List<Subscription> findAllByCompanyId(Long companyId);

    Optional<Subscription> findTopByCompanyIdOrderByIdDesc(Long companyId);
}