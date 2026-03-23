package com.leadmatrix.crm.respository;

import com.leadmatrix.crm.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {


    Optional<Subscription> findByCompanyId(Long companyId);
}