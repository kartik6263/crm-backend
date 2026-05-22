package com.resolion.crm.service;

import com.resolion.crm.enums.CompanyRole;
import com.resolion.crm.enums.ProductCategory;
import com.resolion.crm.enums.ProductManufacturer;
import com.resolion.crm.enums.ProductTaxType;
import com.resolion.crm.dto.ProductRequest;
import com.resolion.crm.dto.ProductResponse;
import com.resolion.crm.entity.ProductEntity;
import com.resolion.crm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public ProductResponse createProduct(String email, Long companyId, ProductRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        if (request.getProductCode() != null && !request.getProductCode().isBlank()) {
            if (productRepository.existsByCompanyIdAndProductCodeIgnoreCase(companyId, request.getProductCode())) {
                throw new RuntimeException("Product code already exists");
            }
        }

        ProductEntity product = ProductEntity.builder()
                .productOwner(request.getProductOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .productName(request.getProductName())
                .productCode(request.getProductCode())
                .vendorName(request.getVendorName())
                .productActive(request.getProductActive())
                .manufacturer(request.getManufacturer())
                .productCategory(request.getProductCategory())
                .salesStartDate(request.getSalesStartDate())
                .salesEndDate(request.getSalesEndDate())
                .supportStartDate(request.getSupportStartDate())
                .supportEndDate(request.getSupportEndDate())
                .unitPrice(request.getUnitPrice())
                .commissionRate(request.getCommissionRate())
                .taxType(request.getTaxType())
                .taxRate(request.getTaxRate())
                .taxable(request.getTaxable())
                .usageUnit(request.getUsageUnit())
                .qtyOrdered(request.getQtyOrdered())
                .quantityInStock(request.getQuantityInStock())
                .reorderLevel(request.getReorderLevel())
                .handler(request.getHandler())
                .quantityInDemand(request.getQuantityInDemand())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .build();

        applyTaxDefaults(product);
        product.calculateValues();

        ProductEntity saved = productRepository.save(product);
        return toResponse(saved);
    }

    public List<ProductResponse> getVisibleProducts(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<ProductEntity> products;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            products = productRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            products = productRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            products = productRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return products.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ProductResponse getProductById(String email, Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateAccess(email, product.getCompanyId());

        return toResponse(product);
    }

    public ProductResponse updateProduct(String email, Long id, ProductRequest request) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateAccess(email, product.getCompanyId());
        validateRequest(request);

        product.setProductOwner(request.getProductOwner());
        product.setOwnerEmail(request.getOwnerEmail());
        product.setProductName(request.getProductName());
        product.setProductCode(request.getProductCode());
        product.setVendorName(request.getVendorName());
        product.setProductActive(request.getProductActive());
        product.setManufacturer(request.getManufacturer());
        product.setProductCategory(request.getProductCategory());
        product.setSalesStartDate(request.getSalesStartDate());
        product.setSalesEndDate(request.getSalesEndDate());
        product.setSupportStartDate(request.getSupportStartDate());
        product.setSupportEndDate(request.getSupportEndDate());
        product.setUnitPrice(request.getUnitPrice());
        product.setCommissionRate(request.getCommissionRate());
        product.setTaxType(request.getTaxType());
        product.setTaxRate(request.getTaxRate());
        product.setTaxable(request.getTaxable());
        product.setUsageUnit(request.getUsageUnit());
        product.setQtyOrdered(request.getQtyOrdered());
        product.setQuantityInStock(request.getQuantityInStock());
        product.setReorderLevel(request.getReorderLevel());
        product.setHandler(request.getHandler());
        product.setQuantityInDemand(request.getQuantityInDemand());
        product.setDescription(request.getDescription());

        applyTaxDefaults(product);
        product.calculateValues();

        ProductEntity saved = productRepository.save(product);
        return toResponse(saved);
    }

    public ProductResponse updateActiveStatus(String email, Long id, Boolean active) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateAccess(email, product.getCompanyId());

        product.setProductActive(active);

        ProductEntity saved = productRepository.save(product);
        return toResponse(saved);
    }

    public void deleteProduct(String email, Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateAccess(email, product.getCompanyId());

        productRepository.delete(product);
    }

    public List<ProductResponse> getActiveProducts(String email, Long companyId) {
        validateAccess(email, companyId);

        return productRepository.findByCompanyIdAndProductActiveOrderByIdDesc(companyId, true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getByCategory(String email, Long companyId, ProductCategory category) {
        validateAccess(email, companyId);

        return productRepository.findByCompanyIdAndProductCategoryOrderByIdDesc(companyId, category)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getByManufacturer(String email, Long companyId, ProductManufacturer manufacturer) {
        validateAccess(email, companyId);

        return productRepository.findByCompanyIdAndManufacturerOrderByIdDesc(companyId, manufacturer)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProducts(String email, Long companyId, String keyword) {
        validateAccess(email, companyId);

        return productRepository.searchByKeyword(companyId, keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countProducts(String email, Long companyId) {
        validateAccess(email, companyId);
        return productRepository.countByCompanyId(companyId);
    }

    public long countActiveProducts(String email, Long companyId) {
        validateAccess(email, companyId);
        return productRepository.countByCompanyIdAndProductActive(companyId, true);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(ProductRequest request) {
        if (request.getProductName() == null || request.getProductName().isBlank()) {
            throw new RuntimeException("Product name is required");
        }

        if (request.getUnitPrice() != null && request.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Unit price cannot be negative");
        }

        if (request.getCommissionRate() != null &&
                (request.getCommissionRate().compareTo(BigDecimal.ZERO) < 0 ||
                        request.getCommissionRate().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new RuntimeException("Commission rate must be between 0 and 100");
        }

        if (request.getTaxRate() != null &&
                (request.getTaxRate().compareTo(BigDecimal.ZERO) < 0 ||
                        request.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new RuntimeException("Tax rate must be between 0 and 100");
        }

        if (request.getSalesStartDate() != null && request.getSalesEndDate() != null &&
                request.getSalesEndDate().isBefore(request.getSalesStartDate())) {
            throw new RuntimeException("Sales end date cannot be before sales start date");
        }

        if (request.getSupportStartDate() != null && request.getSupportEndDate() != null &&
                request.getSupportEndDate().isBefore(request.getSupportStartDate())) {
            throw new RuntimeException("Support end date cannot be before support start date");
        }
    }

    private void applyTaxDefaults(ProductEntity product) {
        if (product.getTaxType() == null) {
            product.setTaxType(ProductTaxType.NONE);
        }

        if (product.getTaxType() == ProductTaxType.SALES_TAX && product.getTaxRate() == null) {
            product.setTaxRate(BigDecimal.ZERO);
        }

        if (product.getTaxType() == ProductTaxType.VAT && product.getTaxRate() == null) {
            product.setTaxRate(BigDecimal.ZERO);
        }

        if (product.getTaxable() == null) {
            product.setTaxable(false);
        }

        if (!Boolean.TRUE.equals(product.getTaxable())) {
            product.setTaxRate(BigDecimal.ZERO);
        }
    }

    private ProductResponse toResponse(ProductEntity product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productOwner(product.getProductOwner())
                .ownerEmail(product.getOwnerEmail())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .vendorName(product.getVendorName())
                .productActive(product.getProductActive())
                .manufacturer(product.getManufacturer())
                .productCategory(product.getProductCategory())
                .salesStartDate(product.getSalesStartDate())
                .salesEndDate(product.getSalesEndDate())
                .supportStartDate(product.getSupportStartDate())
                .supportEndDate(product.getSupportEndDate())
                .unitPrice(product.getUnitPrice())
                .commissionRate(product.getCommissionRate())
                .taxType(product.getTaxType())
                .taxRate(product.getTaxRate())
                .taxable(product.getTaxable())
                .usageUnit(product.getUsageUnit())
                .qtyOrdered(product.getQtyOrdered())
                .quantityInStock(product.getQuantityInStock())
                .reorderLevel(product.getReorderLevel())
                .handler(product.getHandler())
                .quantityInDemand(product.getQuantityInDemand())
                .taxAmount(product.getTaxAmount())
                .commissionAmount(product.getCommissionAmount())
                .totalPrice(product.getTotalPrice())
                .availableStock(product.getAvailableStock())
                .lowStock(product.getLowStock())
                .description(product.getDescription())
                .companyId(product.getCompanyId())
                .createdBy(product.getCreatedBy())
                .createdDate(product.getCreatedDate())
                .updatedDate(product.getUpdatedDate())
                .build();
    }
}