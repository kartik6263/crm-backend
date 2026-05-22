package com.resolion.crm.repository;

import com.resolion.crm.enums.ProductCategory;
import com.resolion.crm.enums.ProductManufacturer;
import com.resolion.crm.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<ProductEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<ProductEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<ProductEntity> findByCompanyIdAndProductActiveOrderByIdDesc(Long companyId, Boolean productActive);

    List<ProductEntity> findByCompanyIdAndProductCategoryOrderByIdDesc(Long companyId, ProductCategory productCategory);

    List<ProductEntity> findByCompanyIdAndManufacturerOrderByIdDesc(Long companyId, ProductManufacturer manufacturer);

    boolean existsByCompanyIdAndProductCodeIgnoreCase(Long companyId, String productCode);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndProductActive(Long companyId, Boolean productActive);

    @Query("""
           SELECT p FROM ProductEntity p
           WHERE p.companyId = :companyId
           AND (
                LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(p.vendorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(p.handler) LIKE LOWER(CONCAT('%', :keyword, '%'))
           )
           ORDER BY p.id DESC
           """)
    List<ProductEntity> searchByKeyword(@Param("companyId") Long companyId,
                                        @Param("keyword") String keyword);
}