package com.resolion.crm.repository;

import com.resolion.crm.enums.ContactLeadSource;
import com.resolion.crm.entity.ContactsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactsRepository extends JpaRepository<ContactsEntity, Long> {

    List<ContactsEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<ContactsEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<ContactsEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<ContactsEntity> findByCompanyIdAndLeadSourceOrderByIdDesc(Long companyId, ContactLeadSource leadSource);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndLeadSource(Long companyId, ContactLeadSource leadSource);

    @Query("""
           SELECT c FROM ContactsEntity c
           WHERE c.companyId = :companyId
           AND (
                LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.accountName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.mobile) LIKE LOWER(CONCAT('%', :keyword, '%'))
           )
           ORDER BY c.id DESC
           """)
    List<ContactsEntity> searchByKeyword(@Param("companyId") Long companyId,
                                         @Param("keyword") String keyword);
}