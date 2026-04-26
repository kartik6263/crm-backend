package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crm_permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private String roleName;
    private String moduleName;

    private Boolean canView = true;
    private Boolean canCreate = false;
    private Boolean canEdit = false;
    private Boolean canDelete = false;

    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public Boolean getCanView() { return canView; }
    public void setCanView(Boolean canView) { this.canView = canView; }

    public Boolean getCanCreate() { return canCreate; }
    public void setCanCreate(Boolean canCreate) { this.canCreate = canCreate; }

    public Boolean getCanEdit() { return canEdit; }
    public void setCanEdit(Boolean canEdit) { this.canEdit = canEdit; }

    public Boolean getCanDelete() { return canDelete; }
    public void setCanDelete(Boolean canDelete) { this.canDelete = canDelete; }
}