package com.resolion.crm.respository;

import com.resolion.crm.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByCompanyIdOrderByRoleNameAsc(Long companyId);
    Optional<Permission> findByCompanyIdAndRoleNameAndModuleName(Long companyId, String roleName, String moduleName);
}