package com.resolion.crm.controller;

import com.resolion.crm.entity.Permission;
import com.resolion.crm.respository.PermissionRepository;
import com.resolion.crm.services.AuditService;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public List<Permission> getPermissions(@RequestParam String email,
                                           @RequestParam Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }

        seedDefaults(companyId);
        return permissionRepository.findByCompanyIdOrderByRoleNameAsc(companyId);
    }

    @PostMapping
    public Permission savePermission(@RequestParam String email,
                                     @RequestParam Long companyId,
                                     @RequestBody Permission permission) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }

        permission.setCompanyId(companyId);

        Permission saved = permissionRepository.save(permission);

        auditService.log(companyId, email, "UPDATE", "PERMISSIONS",
                String.valueOf(saved.getId()), "Permission updated for " + permission.getRoleName());

        return saved;
    }

    private void seedDefaults(Long companyId) {
        createIfMissing(companyId, "OWNER", "REPORTS", true, true, true, true);
        createIfMissing(companyId, "ADMIN", "REPORTS", true, true, true, true);
        createIfMissing(companyId, "SALES", "REPORTS", true, false, false, false);
        createIfMissing(companyId, "USER", "REPORTS", true, false, false, false);

        createIfMissing(companyId, "OWNER", "LEADS", true, true, true, true);
        createIfMissing(companyId, "ADMIN", "LEADS", true, true, true, true);
        createIfMissing(companyId, "SALES", "LEADS", true, true, true, false);
        createIfMissing(companyId, "USER", "LEADS", true, true, false, false);

        createIfMissing(companyId, "OWNER", "ANALYTICS", true, true, true, true);
        createIfMissing(companyId, "ADMIN", "ANALYTICS", true, false, false, false);
        createIfMissing(companyId, "SALES", "ANALYTICS", true, false, false, false);
        createIfMissing(companyId, "USER", "ANALYTICS", false, false, false, false);
    }

    private void createIfMissing(Long companyId, String role, String module,
                                 boolean view, boolean create, boolean edit, boolean delete) {
        if (permissionRepository.findByCompanyIdAndRoleNameAndModuleName(companyId, role, module).isPresent()) {
            return;
        }

        Permission p = new Permission();
        p.setCompanyId(companyId);
        p.setRoleName(role);
        p.setModuleName(module);
        p.setCanView(view);
        p.setCanCreate(create);
        p.setCanEdit(edit);
        p.setCanDelete(delete);

        permissionRepository.save(p);
    }
}