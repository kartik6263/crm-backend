package com.resolion.crm.respository;

import com.resolion.crm.ENUMS.VendorCategory;
import com.resolion.crm.ENUMS.VendorGlAccount;
import com.resolion.crm.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VendorRepository extends JpaRepository<VendorEntity, Long> {

    List<VendorEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<VendorEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<VendorEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<VendorEntity> findByCompanyIdAndGlAccountOrderByIdDesc(Long companyId, VendorGlAccount glAccount);

    List<VendorEntity> findByCompanyIdAndCategoryOrderByIdDesc(Long companyId, VendorCategory category);

    long countByCompanyId(Long companyId);

    @Query("""
           SELECT v FROM VendorEntity v
           WHERE v.companyId = :companyId
           AND (
                LOWER(v.vendorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(v.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(v.website) LIKE LOWER(CONCAT('%', :keyword, '%'))
           )
           ORDER BY v.id DESC
           """)
    List<VendorEntity> searchByKeyword(@Param("companyId") Long companyId,
                                       @Param("keyword") String keyword);
}