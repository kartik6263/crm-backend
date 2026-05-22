package com.resolion.crm.repository;

import com.resolion.crm.enums.SolutionStatus;
import com.resolion.crm.entity.SolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SolutionRepository extends JpaRepository<SolutionEntity, Long> {

    List<SolutionEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<SolutionEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<SolutionEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<SolutionEntity> findByCompanyIdAndStatusOrderByIdDesc(Long companyId, SolutionStatus status);

    List<SolutionEntity> findByCompanyIdAndPublishedOrderByIdDesc(Long companyId, Boolean published);

    Optional<SolutionEntity> findByCompanyIdAndSolutionNumberIgnoreCase(Long companyId, String solutionNumber);

    boolean existsBySolutionNumberIgnoreCase(String solutionNumber);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, SolutionStatus status);

    @Query("""
           SELECT s FROM SolutionEntity s
           WHERE s.companyId = :companyId
           AND (
                LOWER(s.solutionNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.solutionTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.question) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.answer) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
           )
           ORDER BY s.id DESC
           """)
    List<SolutionEntity> searchByKeyword(@Param("companyId") Long companyId,
                                         @Param("keyword") String keyword);
}