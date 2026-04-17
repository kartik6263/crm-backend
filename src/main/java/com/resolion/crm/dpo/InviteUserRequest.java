package com.resolion.crm.dpo;

public class InviteUserRequest {
    private String adminEmail;
    private Long companyId;
    private String invitedEmail;
    private String role;

    public String getAdminEmail() {
        return adminEmail;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public String getRole() {
        return role;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public void setRole(String role) {
        this.role = role;
    }
}