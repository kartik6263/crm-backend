package com.resolion.crm.respository;

import com.resolion.crm.ENUMS.AccountIndustry;
import com.resolion.crm.ENUMS.AccountRating;
import com.resolion.crm.ENUMS.AccountType;
import com.resolion.crm.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<AccountEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<AccountEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<AccountEntity> findByCompanyIdAndAccountTypeOrderByIdDesc(Long companyId, AccountType accountType);

    List<AccountEntity> findByCompanyIdAndIndustryOrderByIdDesc(Long companyId, AccountIndustry industry);

    List<AccountEntity> findByCompanyIdAndRatingOrderByIdDesc(Long companyId, AccountRating rating);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndAccountType(Long companyId, AccountType accountType);

    long countByCompanyIdAndIndustry(Long companyId, AccountIndustry industry);

    long countByCompanyIdAndRating(Long companyId, AccountRating rating);

    @Query("""
           SELECT a FROM AccountEntity a
           WHERE a.companyId = :companyId
           AND (
                LOWER(a.accountName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.website) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.accountOwner) LIKE LOWER(CONCAT('%', :keyword, '%'))
           )
           ORDER BY a.id DESC
           """)
    List<AccountEntity> searchByKeyword(@Param("companyId") Long companyId,
                                        @Param("keyword") String keyword);
}
